(ns cscljs.main
  (:require 
    [clojure.string :as str]
    ["colors" :as colors]
    ["path" :as path]
    ["yargs" :as yargs]
    ["shelljs" :as sh]))

(defn read-args [^js yargs]
  (-> (.options yargs (clj->js {
    :n {
      :alias "name",
      :describe "The name of the project",
      :type "string"
    },
    :d {
      :alias "description",
      :describe "The description of the project",
      :type "string"
    },
    :i {
      :alias "install",
      :describe "If `true` it runs `npm install`. Use `--no-install` to skip.",
      :default true,
      :type "boolean"
    }
  }))
  (.help)
  (.-argv)))

(defn __dirname [] (js* "__dirname"))

(defn make-ctx [^js argv ^js sh]
  (-> {
        ; FIXME Convert `make-ctx` to receive a Clojure map instead of JS
        :name (or (.-name argv) (aget (.-_ argv) 0) "")
        :description (.-description argv)

        :argv (js->clj argv)
        :cwd (.. sh pwd toString)
        :templatesPath (.join path (__dirname) "templates")

        :sh sh
      }
      ((fn [m] (assoc m :projectPath (.join path (.. sh pwd toString) (:name m)))))))

(defn initProjectDir [{:keys [^js sh, name]}]
  (when (str/blank? name)
    (.echo sh (.bgRed colors (.white colors "The project name cannot be empty. Provide one using the -n/--name options.")))
    (.exit sh 1))
  
  (when (some #(= name %) (js->clj (.ls sh ".")))
    (.echo sh (.bgRed colors (.white colors (str "The given directory '" name "' already exists, please choose a different one."))))
    (.exit sh 1))
  
  (.mkdir sh "-p" name))

(defn copyTemplates [{:keys [^js sh, templatesPath, projectPath]}]
  (.echo sh (.bold colors "\t:: Copying project files..."))
  (.cp sh "-rf" (.join path templatesPath "*") projectPath)
  (..
    sh
    (ShellString. (str/join "\n" [
      "build/",
      "node_modules/",
      "target/",
      "/yarn.lock",
      ".shadow-cljs/",
      ".nrepl-port",
    ]))
    (to (.join path projectPath ".gitignore"))))

(defn updatePackageJson [{:keys [^js sh, name, description, projectPath]}]
  (.echo sh (.bold colors "\t:: Updating `package.json`..."))
  (let [projectPkgJson (.join path projectPath "package.json")
        original (js->clj (.parse js/JSON (.. sh (cat projectPkgJson) (toString))))
        updated (-> 
                  original
                  (assoc "name" name) 
                  (assoc "description" 
                    (or description (get original "description"))))]
    (.. sh (ShellString. (.stringify js/JSON (clj->js updated) nil 2)) (to projectPkgJson))))

(defn installDependencies [{:keys [^js sh, ^js argv, cwd, projectPath]}]
  (when (get argv "install" false)
    (.echo sh (.bold colors "\t:: Installing NPM dependencies..."))
    (.cd sh projectPath)
    (.exec sh "npm install")
    (.cd sh cwd)))

(defn initGitRepository [{:keys [^js sh, cwd, projectPath]}]
  (when (.which sh "git")
    (.echo sh (.bold colors "\t:: Initializing .git..."))
    (.cd sh projectPath)
    (.exec sh "git init .")
    (.exec sh "git add --all .")
    (.exec sh "git commit -m 'Initial commit'")
    (.cd sh cwd)))

(defn -main []
  (let [argv (read-args ^js yargs)
        ctx (make-ctx argv ^js sh)]
    (set! (-> sh (.-config) (.-silent)) true)
    (set! (-> sh (.-config) (.-fatal)) true)

    (.echo sh (.bold colors (.green colors ":: Running the `create-shadow-cljs` initializer")))
    (initProjectDir ctx)
    (copyTemplates ctx)
    (updatePackageJson ctx)
    (installDependencies ctx)
    (initGitRepository ctx)
    (.echo sh (.bold colors (.green colors (str ":: Successfully created '" (:name ctx) "'!"))))))
