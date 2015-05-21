(ns labyrinth.core)

;; Provide a sandbox and file-system functionality.
(def empty-attribute-array (make-array java.nio.file.attribute.FileAttribute 0))

(defn- -create-tmp-dir
  "Private wrapper, ignores any file attributes."
  [prefix]
  (java.nio.file.Files/createTempDirectory
   prefix
   empty-attribute-array))

(defn create-tmp-dir
  "Create a temp directory and return the Path instance that references it.
   Takes an optional String that acts as a prefix for the temp directory."
  ([prefix] (-create-tmp-dir prefix))
  ([]       (-create-tmp-dir "")))

(defn join
  "Join two Path instances together."
  [path1 path2]
  (.resolve path1 path2))

(defn path
  "Create an instance of Path from a string.
     If multiple strings are passed in attempt to create a joined path."
  [& strings]
  (let [creator (fn [s]  (.toPath (java.io.File. s)))]
    (reduce join (map creator strings))))

(defn pwd
  "Return the absolute path to the current working directory
   as a Path instance"
  []
  (path (.getAbsolutePath (new java.io.File "."))))

(defn remove-root
  "Remove a root component from a Path instance returning a new Path."
  [path]
  (.subpath path 0  (.getNameCount path)))

(defn sandbox
  "Return a new Path instance with the sandbox-path prepended to path."
  [sandbox-path path]
  (join sandbox-path (remove-root path)))

(defn path-child
  "Get the last element of a Path instance."
  [path]
  (.getFileName path))

(defn path-parents
  "Get the parents of a Path instance."
  [path]
  (.getParent path))

(defn- sandboxify
  "Make a sandboxed version of an IO function."
  [f]
  (fn [sandbox-path path]  (f (sandbox sandbox-path path))))

;; IO functions.
;; The private versions are so we can make with-sandbox work.
;; Probbly the wrong way to do it.

(defn- -mkdirs
  [path]
  (java.nio.file.Files/createDirectories path empty-attribute-array))

(defn mkdirs
  "Create a directory while creating any parent directories that don't already exist"
  [path]
  (-mkdirs path))

(defn- -touch
  [path]
  (-mkdirs (path-parents path))
  (java.nio.file.Files/createFile path empty-attribute-array))

(defn touch
  "Create an empty file, creating any parent directories if they don't exist."
  [path]  (-touch path))

(defmacro with-sandbox [sandbox-path expr]
  `(with-redefs-fn {(var touch)   (partial (sandboxify -touch) ~sandbox-path)
                    (var mkdirs)  (partial (sandboxify -mkdirs) ~sandbox-path)}
     (fn [] ~expr)))

(with-sandbox (create-tmp-dir "prefix")
  (list (touch (path "foo"))
        (touch (path "/root-dir/file.txt"))
        (mkdirs (path "my-folder/somedir"))))

