INSERT INTO `%s`(
  name,
  description,
  collection,
  class_name,
  properties
) VALUES (
"kalman",
"default settings (p<0.03, seasonal=168, knob=10000)",
"%s",
"KalmanAnomalyDetectionFunction",
"# autogenerated properties
metric=%s
pValueThreshold=0.03
trainSize=14
trainUnit=DAYS
bucketSize=1
bucketUnit=HOURS
order=1
knob=10000
seasonal=168
"
);