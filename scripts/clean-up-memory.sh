#!/bin/bash

ps -ef | awk '/squirrel-0.1/{print $2}' | xargs kill
