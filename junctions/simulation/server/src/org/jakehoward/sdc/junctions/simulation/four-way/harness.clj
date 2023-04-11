(ns org.jakehoward.sdc.junctions.simulation.four-way.harness)

(def config {:iterations         1000
             :resolution-ms      100
             :default-speed-kmph 100})

(def default-layout
  {:x-min 0 :x-max 600 :y-min 0 :y-max 400 ;; metres
   :type :four-way
   :n->s {:name "A1"}
   :e->w {:name "A2"}})

(defn spawn-vehicle [existig-vehicles layout road direction speed]
  {:id (java.util.UUID/randomUUID) :road road :direction direction :speed speed :accleration 0})

(defn run [protocol layout config]
  (loop [vehicles   []
         messages   {}
         iterations (:iterations config)]

    ;; Maybe spawn more vehicles
    ;; (spawn-vehicle [] layout :n->s :n (:default-speed-kmph config))
    ;; - some random factor
    ;; - are there existing vehicles in the way

    ;; Loop over all vehicles, run protocol fn
    (let [protocol-output (map (fn [vehicle] (protocol vehicle messages)) vehicles)]
      ;; recur
      ;; - dec iterations
      ;; - index messages by vehicle id
      )))
