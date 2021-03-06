.MtDEVICE=9a303ebc8e323dfa96d7a38106d8b781d00f14cc

all: update install flutter
	echo you can test http://www.hornik.fr:8080/getTable?gameId=99
	echo           or http://www.hornik.fr:8081

help:
	@echo
	@echo make "[all|angular|update|kotlin|flutter|instWeb|instServer]"
	@echo
	@echo "\tangular\t\t:\tGenerate the Angular target"
	@echo "\tkotlin\t\t:\tGenerate the Server"
	@echo "\tupdate\t\t:\tUpdate from git the workspace"
	@echo "\tflutter\t\t:\tCompile the application Flutter"
	@echo "\tios\t\t:\tCompile the application on the first IOS device found"
	@echo "\tinstWeb\t\t:\tCompile and deploy the Web files on the raspBerry Pi"
	@echo "\tinstServer\t:\tCompile and install the Server on the raspberry Pi"
	@echo "\tinstall\t\t:\tCompile and install Web and Server"
	@echo "\tall\t\t:\tDo all of the above tasks"
	
	
update:
	git pull

angular:
	cd Web ; yarn ; yarn run build --prod

kotlin:
	cd Server ; mvn clean install -Dmaven.test.skip=true
	cd Server ; mvn clean package -Dmaven.test.skip=true

instServer: kotlin
	scp Server/target/coinche-0.0.1-SNAPSHOT.jar pi@fraise1:Server/copying.coinche.lock
	ssh pi@fraise1 mv Server/copying.coinche.lock Server/coinche-0.0.1-SNAPSHOT.jar

instWeb: angular
	ssh pi@fraise1 rm -rf /var/www/html/*
	scp -r Web/dist/display-cards/* pi@fraise1:/var/www/html


install: instServer instWeb

flutter:
	cd FlutterCoinche; flutter pub get
	#if the next step fail - rm -rf FlutterCoinche/.dart_tool should solve the issue
	#this directory not cleaned before a build 
	# --> after upgrading flutter build_runner step can fail
	#removing .dart_tool (in FlutterCoinche directory solves) the issue.
	cd FlutterCoinche; dart tool/env.dart ; flutter pub run build_runner build --delete-conflicting-outputs
	cd FlutterCoinche; flutter build ios 
	#to run on device xxxx
	#flutter run -d 420095c6ea425400

iPad:
	cd FlutterCoinche; flutter pub get
	cd FlutterCoinche; dart tool/env.dart ; flutter pub run build_runner build --delete-conflicting-outputs
	cd FlutterCoinche ; flutter run -d ${MYDEVICE}

tutu:
	@ssh pi@fraise1 "rm -rf /var/www/html/*"
	scp -r Web/dist/display-cards/* pi@fraise1:/var/www/html
