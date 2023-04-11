(ns org.jakehoward.sdc.junctions.simulation.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes context GET POST routes]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [compojure.route :as route]))

(def example-layout
  {:x-min 0 :x-max 600 :y-min 0 :y-max 400
   :type :four-way
   :n->s {:name "A1" :lane-width 12 :speed-limit 100}
   :e->w {:name "A2" :lane-width 8 :speed-limit 80}})

(defn example-time-slot-to-vehicles [layout]
  (let [vehicle-length 10
        initial-y   (- (:y-max layout) (inc vehicle-length))
        centre-x    (/ (:x-max layout) 2)
        x           (+ 2 (- centre-x (get-in layout [:n->s :lane-width])))
        time-slots  (dec initial-y)
        example-car {:id 1 :color "#ff0000" :length vehicle-length :width 7 :front-left {:x x :y 11}}]
    (->> (for [s (range time-slots)]
           [s [(assoc-in example-car [:front-left :y] (- initial-y s))]])
         (into {}))))

(defroutes simulation-routes
  (context
    "/api/v1/simulation/junction" []
    (GET "/four-way" [_ :as req]
         (let [protocol       (get-in req [:params :protocol])
               [layout ts->v] (case protocol
                                "example" [example-layout
                                           (example-time-slot-to-vehicles example-layout)])]
           {:body (prn-str {:type :four-way
                            :protocol protocol
                            :layout layout
                            :resolution-ms 50
                            :time-slot-to-vehicles ts->v})
            :headers {"Access-Control-Allow-Origin" "*"
                      "Content-Type" "application/edn"}}))))

(defroutes base-routes
  (GET "/status" [] "ok"))

(def app (-> (routes
              base-routes
              simulation-routes
              (route/not-found "<h1>Page not found</h1>"))
             (wrap-json-body {:keywords? true})
             ;; (wrap-json-response)
             (wrap-defaults api-defaults)))

(defonce server (atom nil))

(defn start-server
  ([]
   (start-server 8081))
  ([port]
   (reset! server (run-jetty app {:port port :join? false}))))

(defn stop-server []
  (when @server
    (.stop @server)))
