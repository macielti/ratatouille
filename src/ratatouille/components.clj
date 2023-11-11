(ns ratatouille.components
  (:require [com.stuartsierra.component :as component]
            [common-clj.component.service :as component.service]
            [common-clj.component.routes :as component.routes]
            [common-clj.component.config :as component.config]
            [common-clj.component.http-client :as component.http-client]
            [common-clj.component.telegram.consumer :as component.telegram.consumer]
            [ratatouille.diplomat.telegram.consumer :as diplomat.telegram.consumer]
            [common-clj.component.datomic :as component.datomic]
            [ratatouille.db.datomic.config :as datomic.config]
            [ratatouille.diplomat.http-server :as diplomat.http-server]))

(def system
  (component/system-map
    :config (component.config/new-config "resources/config.edn" :prod :edn)
    :datomic (component/using (component.datomic/new-datomic-local datomic.config/schemas) [:config])
    :http-client (component/using (component.http-client/new-http-client) [:config])
    :telegram-consumer (component/using (component.telegram.consumer/new-telegram-consumer diplomat.telegram.consumer/consumers) [:config :http-client :datomic])
    :routes (component/using (component.routes/new-routes diplomat.http-server/routes) [:config])
    :service (component/using (component.service/new-service) [:routes :config :datomic])))

(defn start-system! []
  (component/start system))