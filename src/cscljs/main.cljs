(ns cscljs.main
  (:require 
    [clojure.string :as str]
    ["colors" :as colors]
    ["path" :as path]
    ["command-line-args" :as cla]
    ["command-line-usage" :as clu]
    ["shelljs" :as sh]))

(defn read-args [^js cla ^js clu ^js sh]
  (let [
    optionDefs 
    [
      {
        :name "name"
        :alias "n"
        :description "The name of the project"
        :defaultOption true
        :defaultValue nil
        :type js/String
      },
      {
        :name "description"
        :alias "d"
        :description "The description of the project"
        :defaultValue nil
        :type js/String
      },
      {
        :name "no-install"
        :description "Skip `npm install` on the newly created project"
        :defaultValue false
        :type js/Boolean
      }
      {
        :name "help"
        :alias "h"
        :description "Show usage"
        :defaultValue false
        :type js/Boolean
      }
    ]
    options 
    (js->clj (cla (clj->js optionDefs) (clj->js {:partial true})) :keywordize-keys true)]
    (if (:help options)
      (do
        (println 
          (clu (clj->js [
            {:header "Typical Example",
            :content "npx create-shadow-cljs-app <app-name>"}
            {:header "Available options"
            :optionList optionDefs}
            {:content "Project home: {underline https://github.com/lambrospetrou/create-shadow-cljs-app}"}
          ])))
          (js/process.exit 0))
      options)))

(defn make-ctx [args ^js sh]
  {
    :name (:name args)
    :description (:description args)

    :args args
    :cwd (.. sh pwd toString)
    :templatesPath (.join path js/__dirname "templates")
    :projectPath (.join path (.. sh pwd toString) (get args :name ""))

    :sh sh
  })

(defn initProjectDir [{:keys [^js sh name]}]
  (when (str/blank? name)
    (.echo sh (.bgRed colors (.white colors "The project name cannot be empty. Provide one using the -n/--name options.")))
    (js/process.exit 1))
  
  (when (some #(= name %) (js->clj (.ls sh ".")))
    (.echo sh (.bgRed colors (.white colors (str "The given directory '" name "' already exists, please choose a different one."))))
    (js/process.exit 1))
  
  (.mkdir sh "-p" name))

(defn copyTemplates [{:keys [^js sh templatesPath projectPath]}]
  (.echo sh (.bold colors "\t:: Copying project files..."))
  (.cp sh "-rf" (.join path templatesPath "*") projectPath)
  (..
    sh
    (ShellString. (str/join "\n" [
      "build/"
      "node_modules/"
      "target/"
      "/yarn.lock"
      ".shadow-cljs/"
      ".nrepl-port"
    ]))
    (to (.join path projectPath ".gitignore"))))

(defn updatePackageJson [{:keys [^js sh name description projectPath]}]
  (.echo sh (.bold colors "\t:: Updating `package.json`..."))
  (let [projectPkgJson (.join path projectPath "package.json")
        original (js->clj (.parse js/JSON (.. sh (cat projectPkgJson) (toString))))
        updated (-> 
                  original
                  (assoc "name" name) 
                  (assoc "description" 
                    (or description (get original "description"))))]
    (.. sh (ShellString. (.stringify js/JSON (clj->js updated) nil 2)) (to projectPkgJson))))

(defn installDependencies [{:keys [^js sh args cwd projectPath]}]
  (when (not (:no-install args))
    (.echo sh (.bold colors "\t:: Installing NPM dependencies..."))
    (.cd sh projectPath)
    (.exec sh "npm install")
    (.cd sh cwd)))

(defn initGitRepository [{:keys [^js sh cwd projectPath]}]
  (when (.which sh "git")
    (.echo sh (.bold colors "\t:: Initializing .git..."))
    (.cd sh projectPath)
    (.exec sh "git init .")
    (.exec sh "git add --all .")
    (.exec sh "git commit -m 'Initial commit'")
    (.cd sh cwd)))

(defn -main []
  (let [args (read-args cla clu sh)
        ctx (make-ctx args sh)]
    (set! (-> sh (.-config) (.-silent)) true)
    (set! (-> sh (.-config) (.-fatal)) true)

    (.echo sh (.bold colors (.green colors ":: Running the `create-shadow-cljs-app` initializer")))
    (initProjectDir ctx)
    (copyTemplates ctx)
    (updatePackageJson ctx)
    (installDependencies ctx)
    (initGitRepository ctx)
    (.echo sh (.bold colors (.green colors (str ":: Successfully created '" (:name ctx) "'!"))))))
