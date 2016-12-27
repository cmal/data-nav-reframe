(ns data-nav-reframe.config
  (:require [instaparse.core :as insta]
            [clojure.string :as s])
  )

(def debug?
  ^boolean js/goog.DEBUG)

(def icons
  [
   {:id "zmdi-home"    :label [:i {:class "zmdi zmdi-home"}]}
   {:id "zmdi-account" :label [:i {:class "zmdi zmdi-account"}]}
   {:id "zmdi-info"    :label [:i {:class "zmdi zmdi-info"}]}
   ;; {:id "zmdi-plus"    :label [:i {:class "zmdi zmdi-plus"}]}
   ;; {:id "zmdi-delete"  :label [:i {:class "zmdi zmdi-delete"}]}
   ;; {:id "zmdi-undo"    :label [:i {:class "zmdi zmdi-undo"}]}
   ]
  )

(def home-choices
  [
   {:id "PE" :label "PE" :group "fundamental"}
   {:id "PB" :label "PB" :group "fundamental"}
   {:id "k" :label "k线" :group "price"}
   {:id "md5" :label "MD5" :group "price"}
   {:id "md20" :label "MD20" :group "price"}
   {:id "md60" :label "MD60" :group "price"}
   {:id "egr" :label "净利增速" :group "Growth Rate"}
   {:id "rgr" :label "营收增速" :group "Growth Rate"}
   ])

(def tree
  {:label "APIs"
   :items
   [{:label "dashboard"
     :items
     [{:label "stock"
       :items
       [{:label "test" :method "GET" :url "/test"}
        {:label "test2" :method "GET" :url "/test2"}
        {:label "dashboard" :method "GET" :url "/dashboard/:secucode" :secucode ""}
        {:label "kchart" :method "GET" :url "/kchartdata/:secucode" :secucode ""}
        {:label "latestinfo" :method "GET" :url "/stock/latestinfo/:secucode" :secucode ""}
        {:label "realtime info" :method "GET" :url "/stock/realtimeinfo" :params [:stockids ""]} ;; should be stockid separated by `,`
        {:label "fenshitu" :method "GET" :url "/stock/fenshitu" :params [:stockid ""]}
        {:label "klinedata" :method "GET" :url "/stock/klinedata" :params [:stockid "" :period ""]}
        ;; {:label "priceforward" :method "GET" :url "/stock/priceforward"}
        {:label "testprices" :method "GET" :url "/stock/testprices/:secucode" :secucode ""}
        {:label "placement" :method "GET" :url "/stock/placementinfo"}
        {:label "newstock" :method "GET" :url "/stock/newstockinfo"}
        {:label "basicinfo" :method "GET" :url "/stock/basicinfo/:secucode" :secucode ""}
        ;; {:label "closeprices" :method "GET" :url "/stock/closeprices" :params [:stockid "" :startdate "" :enddate ""]}
        ;; {:label "dividend" :method "GET" :url "/stock/dividend"}
        {:label "tradingcalendar"}
        {:label "istradingday"}
        {:label "pretradingday"}
        {:label "pepbs" :method "GET" :url "/stock/pepbs/:secucode" :secucode ""}
        {:label "logpepbs" :method "GET" :url "/stock/logpepbs/:secucode" :secucode ""}
        {:label "earning" :method "GET" :url "/stock/earning/:secucode" :secucode ""}
        {:label "revenue" :method "GET" :url "/stock/revenue/:secucode" :secucode ""}
        {:label "namefinder" :method "GET" :url "/stock/namefinder" :params [:keyword "" :count ""]}
        {:label "announcements" :method "GET" :url "/stock/announcements" :params [:secucode ""]}
        {:label "stockidlist" :method "GET" :url "/stockidlist"}
        {:label "merger" :method "GET" :url "/merger/:id" :id ""}
        {:label "internal" :method "GET" :url "/internal/:id" :id ""}
        ]}
      {:label "fund"
       :items
       [{:label "basic info"}
        {:label "gradefund"}
        {:label "closeprices"}
        {:label "netvalues"}
        ]}
      {:label "bond"}
      {:label "licai"}
      {:label "user"}
      ]}
    {:label "stock summary"}]})

(def url-prefix "/stockinfogate")

;; returns ([:keyword "asdfas"] [:keyword "id"] [:seg "asdf"])
(def url-parser
  (insta/parser "
<S> = (A+|B+)* <C?>
<A> = < '/' > seg;
<B> = < '/' > keyword;
C = '/';
seg = #'[A-Za-z0-9]+';
keyword = < ':' > #'[A-Za-z0-9]+';
"))

(defn gen-str [mp [k v]]
  (if (= :keyword k)
    (get mp (keyword v))
    v))

(defn get-url
  [parser f mp]
  (str "/" (s/join "/" (map #(f mp %) (parser (:url mp))))))


;; USAGE:
;; (get-url
;;  url-parser
;;  gen-str
;;  {:url "/stock/:id/:date/dsaf" :id "600123.SH" :date "20161111"})
