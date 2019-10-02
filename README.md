# Telegram sender
## Description
This is standalone application for sending telegram notification to users.
API is quite simple:
You just should send get request to TGSENDER_HOSTNAME:8080/sendMsg with parameters "chatId" and "text", where

chatid - telegram chat identifecation number

text - message to send

## Startup
To complile you need jdk 1.7 or above and maven. Then run:

`mvn clean install`

To start application you need jre 1.7 or above,and you should specify environment variables:

`TGNAME=BOT_NAME`
`TGTOKEN=BOT_TOKEN`

After all properties has been set hust start with:

`java -jar TgSender-1.0-jar-with-dependencies.jar`

I use this appliaction deployed in Docker for sending messages from Zabbix server to Telegram using following script:

```#!/bin/bash
wget -O- "http://TGSENDER_HOSTNAME:8080/sendMsg?chatId=$1&text=${2// /%20}"
```
