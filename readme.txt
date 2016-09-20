
NEED HADOOP FOR WINDOWS...


Download Hadoop 2.6.0 compiled for Windows from http://static.barik.net/software/hadoop-2.6.0-dist/hadoop-2.6.0.tar.gz (http://www.barik.net/archive/2015/01/19/172716/)
Create HADOOP_HOME environment variable pointing to the unzipped dir
Add %HADOOP_HOME%\bin to PATH env var



run from command line 

C:\devtools\spark-1.6.2-bin-hadoop2.6\spark-1.6.2-bin-hadoop2.6\bin>spark-submit
 --jars C:\devtools\spark-1.6.2-bin-hadoop2.6\spark-1.6.2-bin-hadoop2.6\lib\mong
o-hadoop-core-1.4.2.jar,C:\devtools\spark-1.6.2-bin-hadoop2.6\spark-1.6.2-bin-ha
doop2.6\lib\mongodb-driver-3.2.2.jar,C:\devtools\spark-1.6.2-bin-hadoop2.6\spark
-1.6.2-bin-hadoop2.6\lib\mongo-java-driver-3.2.2.jar --class "com.scuti.spark.jo
bs.DataframeExample" --master local --deploy-mode client C:\kapil\scuti\spark-jo
bs\target\spark-jobs-0.0.1-SNAPSHOT.jar


-- sample application for just reading in SQL style query
spark-submit --jars C:\devtools\spark-1.6.2-bin-hadoop2.6\spark-1.6.2-bin-hadoop2.6\lib\mongo-hadoop-core-1.4.2.jar,C:\devtools\spark-1.6.2-bin-hadoop2.6\spark-1.6.2-bin-hadoop2.6\lib\mongodb-driver-3.2.2.jar,C:\devtools\spark-1.6.2-bin-hadoop2.6\spark-1.6.2-bin-hadoop2.6\lib\mongo-java-driver-3.2.2.jar --class "com.scuti.spark.jobs.DataframeExample" --master local --deploy-mode client C:\kapil\scuti\spark-jobs\target\spark-jobs-0.0.1-SNAPSHOT.jar


ScutiRecommendation ratings

spark-submit --jars C:\devtools\spark-1.6.2-bin-hadoop2.6\spark-1.6.2-bin-hadoop2.6\lib\mongo-hadoop-core-1.4.2.jar,C:\devtools\spark-1.6.2-bin-hadoop2.6\spark-1.6.2-bin-hadoop2.6\lib\mongodb-driver-3.2.2.jar,C:\devtools\spark-1.6.2-bin-hadoop2.6\spark-1.6.2-bin-hadoop2.6\lib\mongo-java-driver-3.2.2.jar --class "com.scuti.spark.jobs.ScutiRecommendation" --master local --deploy-mode client C:\kapil\scuti\spark-jobs\target\spark-jobs-0.0.1-SNAPSHOT.jar



