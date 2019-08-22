(ns clograms.events
  (:require [re-frame.core :as re-frame]
            [clograms.db :as db]
            [reagent.dom :as rdom]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [datascript.core :as d]
            ["@projectstorm/react-diagrams" :as storm]
            ["@projectstorm/react-diagrams-defaults" :as storm-defaults]
            ))

;; (d/pull @db-conn '[:project/name {:project/dependency 6}] 2)
;; (d/pull @db-conn '[:project/name :project/dependency] 1)


(re-frame/reg-event-fx
 ::initialize-db
 (fn [_ _]
   {:db db/default-db
    :dispatch [::reload-db]}))

(re-frame/reg-event-fx
 ::reload-db
 (fn [cofxs [_]]
   {:http-xhrio {:method          :get
                 :uri             "http://localhost:3000/db"
                 :timeout         8000 ;; optional see API docs
                 :response-format (ajax/raw-response-format) ;; IMPORTANT!: You must provide this.
                 :on-success      [::db-loaded]
                 :on-failure      [:bad-http-result]}}))

(re-frame/reg-event-db
 ::db-loaded
 (fn [db [_ new-db]]
   (let [datascript-db (cljs.reader/read-string new-db)
         main-project-id (d/q '[:find ?pid .
                                :in $ ?pn
                                :where [?pid :project/name ?pn]]
                              datascript-db
                              'clindex/main-project)]
     (js/console.log "Have " (count datascript-db) "datoms. Main project id " main-project-id)
     (assoc db
            :datascript/db datascript-db
            :main-project/id main-project-id))))

(re-frame/reg-event-fx
 ::add-entity-to-diagram
 (fn [{:keys [db]} [_ e {:keys [link-to-selected? x y] :as opts}]]
   {:clograms.diagrams/add-node (cond-> {:entity e
                                         :x (or x 500)
                                         :y (or y 500)
                                         :on-node-selected [::select-node]
                                         :on-node-unselected [::unselect-node]
                                         }
                                  link-to-selected? (assoc :link-to {:id (get-in db [:diagram :selected-node :storm.entity/id])
                                                                     :port  (case (:type e)
                                                                              :call-to :out
                                                                              :called-by :in)}))}))

(re-frame/reg-event-db
 ::select-node
 (fn [db [_ e]]
   (assoc-in db [:diagram :selected-node] e)))

(re-frame/reg-event-db
 ::unselect-node
 (fn [db [_ e]]
   (assoc-in db [:diagram :selected-node] nil)))


(comment

  (re-frame/dispatch [::add-entity-to-diagram
                      {:type :function
                       :namespace/name "cljs.core"
                       :var/name "map2"}
                      {}])
  )
