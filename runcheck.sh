mvn --batch-mode clean package
mvn --batch-mode checkstyle:checkstyle >check.out
mvn --batch-mode spotbugs:check >>check.out
mvn --batch-mode site