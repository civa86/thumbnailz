# thumbnailz

Thumbnail generator library for Clojure.

Based on [mikera/imagez](https://github.com/mikera/imagez) this library is useful to convert, resize and crop images.

## Documentation

#### REPL

```clojure
(use 'mikera.image.core)
```

#### png-path?

Usage: `(png-path? image-path)`
Returns true if image-path is a string that ends with .png extension

Example: 
```clojure
(png-path? "some/dir/file.png")
=> true

(png-path? "some/dir/file.jpg")
=> false
```

#### change-path-extension

Usage: `(change-path-extension file-path new-ext)`
Returns file-path with new-ext as file extension

Example: 
```clojure
(change-path-extension "./image.jpg" "png")
=> "./image.png"
```

#### apply-suffix-to-filename

Usage: `(apply-suffix-to-filename file-path suffix)`
Returns file-path with suffix appended to the file name

Example: 
```clojure
(apply-suffix-to-filename "./image.png" "_WxH")
=> "./image_WxH.png"

(apply-suffix-to-filename "./image.png" "-circle")
=> "./image-circle.png"
```

#### load-image-from-path

Usage: `(png-path? image-path)`
Returns a BufferedImage loaded from image-path

#### get-image-info

Usage: `(png-path? image-path)`
Returns informations about the image located at image-path

#### convert-to-png

Usage: `(png-path? image-path)`
Returns the path of the new png created from image-path file
Saves the new png at the same level of image-path
   
#### create-thumbnail

Usage: `(png-path? image-path)`
Returns the path of a new image resized from image-path with width and height
Saves the new png at the same level of image-path.
   
#### crop-circle

Usage: `(png-path? image-path)`
Returns the path of a new circle image cropped from image-path
Saves the new png at the same level of image-path

## License

MIT.
