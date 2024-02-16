build: maven

maven: export JAVA_HOME = /Users/neko/.sdkman/candidates/java/21.0.2.fx-librca

maven:
	./mvnw clean package
