# move-anime

This thing exists purely because I can't seem to get Filebot working properly on the weird
shared environment that my Plex server exists on, and I have more time to poorly code Clojure
than I do to manually move files.

As a result, this is a very specialized tool built to my exact use case. 

What happens is a torrent file will complete, having been initialized by a release being added to my [ShanaProject](http://shanaproject.com) feed. Upon completion, this program is launched from the completion hook to determine Plex-proper naming for the file and copy it to the expected location.

The intent? Hands-off anime-watching.

## Installation

Requires [Lein](https://leiningen.org/) to build.
Requires a ShanaProject feed for new releases to match against for titles (and knowledge that the file is not some random file).

## Usage

```bash
java -jar move-anime-0.1.0-standalone.jar [filename] [filepath] [output-path]
```

## Examples

```bash
java -jar move-anime-0.1.0-standalone.jar "[HorribleSubs] Citrus - 01 [720].mkv" "/path/to/[HorribleSubs] Citrus - 01 [720].mkv" "/path/to/media/base"
```

### Bugs

Hoo gurl.

## License

Copyright © 2018

Distributed under the MIT license. Modify to your own usage.
