# Wireless Fluid Terminal (Beta)

## Table of Contents

* [About](#about)
* [Contact](#contact)
* [License](#license)
* [Downloads](#downloads)
* [Installation](#installation)
* [Issues](#issues)
* [Building](#building)
* [Contribution](#contribution)
* [API](#wireless-fluid-terminal-api)
* [Localization](#wireless-fluid-terminal-localization)

## About

An OP addon to an already OP mod =]
Adds a Wireless Terminal version of the AE2 Fluid Terminal with infinite range functionality + other features!

Currently in BETA! I need testers! Drop me a line on [Discord](#contact) or [Twitter](#contact)

## Contact

* [Website](http://p455w0rd.net/)
* [Discord](http://p455w0rd.net/discord)
* [GitHub](https://github.com/p455w0rd/WirelessFluidTerminal)
* [Twitter](https://twitter.com/TheRealp455w0rd)

## License

I'm a huge fan of Open Source software as well as helping people learn.
As far as licensing, do what you want. Claim it as your own if you so wish. I don't care =D If you become popular/rich/get a hot wife due to claiming that you made my mod,
I'm just glad I could help make another person's life better in some way.

## Downloads

* RV6 version is available on [CurseForge](http://minecraft.curseforge.com/projects/wireless-fluid-terminal)

## Installation

You install this mod by putting it into the `minecraft/mods/` folder. It requires that [AE2 RV3 Beta](http://ae-mod.info/Downloads/) (any build) also be installed.

## Issues/Feature Requests

* Post 'em in the [issues](https://github.com/p455w0rd/WirelessFluidTerminal/issues) section. =D

Providing as many details as possible does help us to find and resolve the issue faster and also you getting a fixed version as fast as possible.

## Building

1. Clone this repository via 
  - SSH `git clone git@github.com:p455w0rd/WirelessFluidTerminal.git` or 
  - HTTPS `git clone https://github.com/p455w0rd/WirelessFluidTerminal.git`
2. Setup workspace 
  - Decompiled source `gradlew setupDecompWorkspace`
  - Obfuscated source `gradlew setupDevWorkspace`
  - CI server `gradlew setupCIWorkspace`
3. Build `gradlew build`. Jar will be in `build/libs`

## Contribution

* Fork -> Edit -> PR

If you are only doing single file pull requests, GitHub supports using a quick way without the need of cloning your fork. Also read up about [synching](https://help.github.com/articles/syncing-a-fork) if you plan to contribute on regular basis.

## Wireless Fluid Terminal API

### Wireless Fluid Terminal
To make your item a Wireless Fluid Terminal variant, register like normal with AE2 and implement
`p455w0rd.wft.api.IWirelessFluidTerminalItem`

To open the WCT Gui from said item, use
`WCTApi.instance().openWirelessFluidTerminalGui(EntityPlayer player);`

### WFT Configs
Configs have been moved to [AE2WTLib](https://github.com/p455w0rd/AE2WirelessTerminalLibrary)

### Maven

When compiling against the WFT API you can use gradle dependencies, just add

	repositories {
		maven {
			name = "covers Maven"
			url = "http://maven.covers1624.net"
		}
	}

    dependencies {
		deobfCompile "p455w0rd:WirelessFluidTerminal:<MC_VERSION>-<MOD_VERSION>"
        compile "p455w0rd:WirelessFluidTerminal:<MC_VERSION>-<MOD_VERSION>:api"
    }
	

or add the compile line to your existing dependencies task to your build.gradle

An example string would be `p455w0rd:WirelessFluidTerminal:1.12.2-3.10.77:api` for the API only or `p455w0rd:WirelessFluidTerminal:1.12.2-3.10.77`  for the whole mod.

## Wireless Fluid Terminal Localization

### English Text

`en_us` is included in this repository, fixes to typos are welcome.

### Encoding

Files must be encoded as UTF-8.

### New Translations

I would love for someone to do translations for me =]


