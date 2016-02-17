(ns music-mashup-node-cljs.helpers)

(defn find-nested
	[m k]
	(->> (tree-seq coll? identity m)
	(filter map?)
	(some k)))

(defn find-sveral-nested
	[m k]
	(->>
		(tree-seq coll? identity m)
		(filter map?)
		(filter k)))
