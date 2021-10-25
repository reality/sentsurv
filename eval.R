library(readr)
library(dplyr)
avgs <- read_delim("sentsurv/fixed_merged.tsv", 
                       "\t", escape_double = FALSE, trim_ws = TRUE)


sd(avgs$VeryPositive)
avgs[avgs$Negative > (mean(avgs$Negative) + (sd(avgs$Negative) * 1)) & avgs$count > 10,]

sum(cor(avgs$DO_VeryPositive, avgs$MESH_VeryPositive),
cor(avgs$DO_Positive, avgs$MESH_Positive),
cor(avgs$DO_Neutral, avgs$MESH_Neutral),
cor(avgs$DO_Negative, avgs$MESH_Negative),
cor(avgs$DO_VeryNegative, avgs$MESH_VeryNegative)) / 5


avg2 <- avgs %>% as_tibble() %>% mutate(
  DOWin = ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_Negative, "Negative", 
                ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_VeryNegative, "VeryNegative",
                ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_Neutral, "Neutral",
                ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_Positive, "Positive",
                ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_VeryPositive, "VeryPositive", "WTF"
                ))))),
    MESHWin = ifelse(pmax(MESH_VeryPositive, MESH_Positive, MESH_Neutral, MESH_Negative, MESH_VeryNegative) == MESH_Negative, "Negative", 
                ifelse(pmax(MESH_VeryPositive, MESH_Positive, MESH_Neutral, MESH_Negative, MESH_VeryNegative) == MESH_VeryNegative, "VeryNegative",
                ifelse(pmax(MESH_VeryPositive, MESH_Positive, MESH_Neutral, MESH_Negative, MESH_VeryNegative) == MESH_Neutral, "Neutral",
                ifelse(pmax(MESH_VeryPositive, MESH_Positive, MESH_Neutral, MESH_Negative, MESH_VeryNegative) == MESH_Positive, "Positive",
                ifelse(pmax(MESH_VeryPositive, MESH_Positive, MESH_Neutral, MESH_Negative, MESH_VeryNegative) == MESH_VeryPositive, "VeryPositive", "WTF"
                )))))
)

nrow(avg2[avg2$DOWin == 'VeryNegative',])
nrow(avg2[avg2$DOWin == 'Negative',])
nrow(avg2[avg2$DOWin == 'Neutral',])
nrow(avg2[avg2$DOWin == 'Positive',])
nrow(avg2[avg2$DOWin == 'VeryPositive',])

nrow(avg2[avg2$MESHWin == 'VeryNegative',])
nrow(avg2[avg2$MESHWin == 'Negative',])
nrow(avg2[avg2$MESHWin == 'Neutral',])
nrow(avg2[avg2$MESHWin == 'Positive',])
nrow(avg2[avg2$MESHWin == 'VeryPositive',])


nrow(avg2[avg2$MESHWin == 'VeryNegative' & avg2$DOWin == 'VeryNegative',])
nrow(avg2[avg2$MESHWin == 'Negative' & avg2$DOWin == 'Negative',])
nrow(avg2[avg2$MESHWin == 'Neutral' & avg2$DOWin == 'Neutral',])
nrow(avg2[avg2$MESHWin == 'Positive' & avg2$DOWin == 'Positive',])
nrow(avg2[avg2$MESHWin == 'VeryPositive' & avg2$DOWin == 'VeryPositive',])

nrow(avg2[avg2$MESHWin == avg2$DOWin,]) / nrow(avg2)

library(psych)


mtx <- as.matrix(avg2$DOWin, avg2$MESHWin)
ck <- cohen.kappa(mtx)

avg2[avg2$MESHWin == 'Positive' & avg2$DOWin == 'Positive',]



library(readr)
library(dplyr)
cavgs <- read_delim("sentsurv/only_cerebro.tsv", 
                   "\t", escape_double = FALSE, trim_ws = TRUE)


cavg2 <- cavgs %>% as_tibble() %>% mutate(
  DOWin = ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_Negative, "Negative", 
                 ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_VeryNegative, "VeryNegative",
                        ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_Neutral, "Neutral",
                               ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_Positive, "Positive",
                                      ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_VeryPositive, "VeryPositive", "WTF"
                                      ))))))

davgs <- read_delim("sentsurv/death_doid_sentiments_propagated.tsv", 
                    "\t", escape_double = FALSE, trim_ws = TRUE)


davg2 <- davgs %>% as_tibble() %>% mutate(
  DOWin = ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_Negative, "Negative", 
                 ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_VeryNegative, "VeryNegative",
                        ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_Neutral, "Neutral",
                               ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_Positive, "Positive",
                                      ifelse(pmax(DO_VeryPositive, DO_Positive, DO_Neutral, DO_Negative, DO_VeryNegative) == DO_VeryPositive, "VeryPositive", "WTF"
                                      ))))))
