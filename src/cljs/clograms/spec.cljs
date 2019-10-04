(ns clograms.spec
  (:require [clojure.spec.alpha :as s]
            [clograms.re-grams.re-grams-spec :as rg-spec]
            [clograms.re-grams.re-grams :as rg]))

;;;;;;;;;;;; More general specs

(s/def :project/id number?)
(s/def :project/name symbol?)
(s/def :namespace/id number?)
(s/def :namespace/name symbol?)
(s/def :datascript/db any?)
;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def ::x number?)
(s/def ::y number?)

(s/def ::selected-side-bar-tab #{:projects-browser :selected-browser})

(s/def ::side-bar (s/keys :req-un [::selected-side-bar-tab]))

(s/def ::level #{0 1 2})

(s/def ::selected-project (s/nilable :project/id))

(s/def ::selected-namespace (s/nilable :namespace/id))

(s/def ::projects-browser (s/keys :req-un [::level
                                           ::selected-project
                                           ::selected-namespace]))
(s/def ::selected-color string?)

(s/def ::label string?)
(s/def ::dispatch vector?)

(s/def ::menu-entry (s/keys :req-un [::label ::dispatch]))

(s/def ::menu (s/coll-of ::menu-entry))

(s/def ::ctx-menu (s/nilable (s/keys :req-un [::x ::y ::menu])))

(s/def :main-project/id :project/id)

(s/def ::loading? boolean?)

(s/def :namespace/colors (s/map-of :namespace/name string?))
(s/def :project/colors (s/map-of :project/name string?))

(s/def :entity/type #{:var :namespace :project})

(s/def ::db (s/keys :req [::rg/diagram]
                    :opt [:datascript/db
                          :main-project/id
                          :namespace/colors
                          :project/colors]
                    :req-un [::side-bar
                             ::projects-browser
                             ::selected-color
                             ::loading?]
                    :opt-un [::ctx-menu]))
