
all: update install flutter
	echo you can test http://www.hornik.fr:8080/getTable?gameId=99
	echo           or http://www.hornik.fr:8081

help:
	@echo make "[all|angular|update|kotlin|flutter]"

update:
	git pull

angular:
	cd Web ; yarn ; yarn run build --prod

kotlin:
	cd Server ; mvn clean install -Dmaven.test.skip=true
	cd Server ; mvn clean package -Dmaven.test.skip=true

install: angular kotlin
	scp Server/target/coinche-0.0.1-SNAPSHOT.jar pi@fraise1:Server/copying.coinche.lock
	ssh pi@fraise1 mv Server/copying.coinche.lock Server/coinche-0.0.1-SNAPSHOT.jar
	scp -r Web/dist/display-cards/* pi@fraise1:/var/www/html

flutter:
	cd FlutterCoinche; flutter pub get
	cd FlutterCoinche; dart tool/env.dart ; flutter pub run build_runner build --delete-conflicting-outputs
	cd FlutterCoinche; flutter build ios 
	
	#perhaps you will have to run : flutter pub get
