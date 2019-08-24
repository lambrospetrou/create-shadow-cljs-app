const yargs = require("yargs");
const sh = require("shelljs");
const colors = require("colors/safe");
const path = require("path");

const argv = yargs.options({
  'n': {
    alias: 'name',
    describe: "The name of the project",
    type: 'string'
  },
  'd': {
    alias: 'description',
    describe: "The description of the project",
    type: 'string'
  },
  'i': {
    alias: "install",
    describe: "If `true` it runs `npm install`. Use `--no-install` to skip.",
    default: true,
    type: 'boolean'
  }
})
.coerce('name', (name) => {
  if (0 === name.length) {
    throw new Error("-n/--name cannot be empty");
  }
  return name.trim();
})
.help()
.argv;

sh.config.silent = true;
sh.config.fatal = true;

sh.echo(colors.bold(colors.green(":: Running the `create-shadow-cljs` initializer")));

const projectDetails = {
  name: argv.name || argv._[0],
  description: argv.description
};

const initProjectDir = ({name}) => {
  if (!name) {
    sh.echo(colors.bgRed(colors.white(`The project name cannot be empty. Provide one using the -n/--name options.`)));
    sh.exit(1);
  }

  if (sh.ls(".").some(filename => filename === name)) {
    sh.echo(colors.bgRed(colors.white(`The given directory '${name}' already exists, please choose a different one.`)));
    sh.exit(1);
  }
  sh.mkdir("-p", name);
};

const copyTemplates = ({name}) => {
  sh.echo(colors.bold("\t:: Copying project files..."));
  sh.cp("-rf", path.join(__dirname, "..", "templates", "*"), name);
  sh.cp(path.join(__dirname, "..", "templates", ".gitignore"), name);
};

const updatePackageJson = ({name, description}) => {
  sh.echo(colors.bold("\t:: Updating `package.json`..."));
  const projectPkgJson = path.join(`${name}`, "package.json");
  const original = JSON.parse(sh.cat(projectPkgJson).toString());
  const updated = {
    ...original,
    name,
    description: description || original.description
  };
  new sh.ShellString(JSON.stringify(updated, null, 2)).to(projectPkgJson);
};

const installDependencies = ({name}) => {
  if (!argv.install) return;

  sh.echo(colors.bold("\t:: Installing NPM dependencies..."));
  sh.cd(name);
  sh.exec("npm install");
  sh.cd("..");
};

const initGitRepository = ({name}) => {
  if (!sh.which("git")) return;

  sh.echo(colors.bold("\t:: Initializing .git..."));
  sh.cd(name);
  sh.exec("git init .");
  sh.exec("git add --all .");
  sh.exec("git commit -m 'Initial commit'");
  sh.cd("..");
}

initProjectDir(projectDetails);
copyTemplates(projectDetails);
updatePackageJson(projectDetails);
installDependencies(projectDetails);
initGitRepository(projectDetails);

sh.echo(colors.bold(colors.green(`:: Successfully created '${projectDetails.name}'!`)));
