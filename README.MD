![Imgur](https://i.imgur.com/JePMzfE.png "CopyCatch logo")


CopyCatch was the senior project of myself and [Brandon Tran](https://gitlab.cs.wwu.edu/tranb7). It was developed for
teachers to analyze similarity of C++ based coding assignments. It determines a percentage of likeness with other 
assignments which can then be reviewed to determine if students shared code among each other.

![Imgur](https://i.imgur.com/nPXuYNG.gif "CopyCatch Demo")

Once Scanned, the assignments can be viewed side-by-side with the built-in code viewer.

![Imgur](https://i.imgur.com/dO1fTUX.gif "CopyCatch Code Viewer Demo")

## Getting Started

All you need to run CopyCatch is Java runtime on your computer and the CopyCatch runnable jar file which you can 
[download from the releases section](https://github.com/Joshua-Styger/CopyCatch/releases).

If you want to download and compile the source code, I will get a build script of some kind up here to make it easier. The best option at the time of writing is to grab the source files and pop them into an IDE project and build/run/generate a jar that way. If you go this route, it will need a few libraries

* [ReactFX](https://github.com/TomasMikula/ReactFX)
* [RichTextFX](https://github.com/FXMisc/RichTextFX)
* [UndoFX](https://github.com/FXMisc/UndoFX)
* [WellBehavedFX](https://github.com/FXMisc/WellBehavedFX)
* [Flowless](https://github.com/FXMisc/Flowless)

## Roadmap

Features I intend to implement.

* ~~Add, delete, and print relations~~ (Completed 8/17/2019)
* ~~Insert tuples into relations~~ (Completed 8/17/2019)
* ~~Add parsing for files containing pre-made lists of commands~~ (Completed 8/17/2019)
* Delete tuples from relations using qualifiers.
* Import and export relations as XML files.
* Perform "Join", "Select", and "Project" on relations.
* Get unions, intersections, and set differences as well as copys of relations.
* Create relation "views".

## Technologies used

* [XStream](https://x-stream.github.io/) - Used to export relations to XML files.

## Installation
