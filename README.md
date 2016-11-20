# bemmet-idea
Bemmet plugin for Idea to expand [bemmet](https://github.com/tadatuta/bemmet) abbreviation into [BEMJSON](https://en.bem.info/technology/bemjson/).

## Install

Install `bemmet` with npm
```
npm i -g bemmet
```

**You need to have [Node.js](http://nodejs.org) installed.**  
Make sure it's in your `$PATH` by running `node -v` in your command-line.  
On OS X you need to make sure it's in `/usr/local/bin` or symlink it there.


Download [bemmet-idea.jar](https://github.com/amel-true/bemmet-idea/raw/master/bemmet-idea.jar) and install plugin (use "instal plugin from disk..." option)

* Go to preferences, bemmet plugin page (Preferences -> Tools -> Bemmet plugin)
* Set the path to the nodejs interpreter bin file.
* Set the path to the bemmet source.
  * On Mac/Unix should point to ```/usr/local/lib/node_modules/bemmet```
  * On Windows should point to  ````C:\Users\<username>\AppData\Roaming\npm\bemmet\```

## Getting started

Select text, open "Refactor" menu and choose "text2bemjson" option.

### Keyboard shortcut

Default shortcut cmd(ctrl) + shift + b. You can change shortcut in Idea settings.
