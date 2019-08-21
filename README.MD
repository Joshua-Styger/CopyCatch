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

## Running

In order to run CopyCatch, you will need to use the directory chooser to select a folder (or enter the folder manually in the text box) with 2 or more .cpp files in it (all other filetypes will be ignored). You then simply clock the "Run" button and the program will analyze the files for similarity.

## Built With
* [diff-match-patch](https://github.com/google/diff-match-patch) - used for comparing tokenized strings.

## Authors
* Joshua Styger - File processing
* Brandon Tran - UI Design and Code Viewer programming.

## License
* This project is licensed under the Apache 2.0 License - see the [LICENSE.md](https://github.com/Joshua-Styger/CopyCatch/blob/master/LICENSE.md) file for details