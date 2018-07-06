#!/usr/bin/env python
import pika
import re
import main

connection = pika.BlockingConnection(pika.ConnectionParameters(
    host='localhost'))
channel = connection.channel()


channel.queue_declare(queue='ckan')

def callback(ch, method, properties, body):
    #print(" [x] Received %r" % body)
    if body == "hi ckan":
        print(" [x] Received %r" % body)  #TODO:JAVAEND FOR SENDING SPECIFIC MESSAGES INSTEAD OF ALL MESSAGES
        channel.basic_publish(exchange='',
                              routing_key='ckan2',
                              body='hello component')
        channel.queue_declare(queue='ckan')

    elif re.match('https?://(?:[-\w.]|(?:%[\da-fA-F]{2}))+', body):
        print(" [x] Received %r" % body)
        urls = re.findall('https?://(?:[-\w.]|(?:%[\da-fA-F]{2}))+', body)
        urlse = str(urls[0])
        print("sending url")
        try:
            result = main.dump(urlse)
        except Exception:
            print("exception caught")
        finally:
            if(result[0] == 0):
                channel.basic_publish(exchange='', routing_key='ckan2', body='finished dumping')
                if(result[1] == 0):
                    channel.basic_publish(exchange='', routing_key='ckan2', body='ckan crawler exited')
            elif(result[0] == 1):
                channel.basic_publish(exchange='', routing_key='ckan2', body='Error# error processing ckan url')
                if(result[1] == 0):
                    channel.basic_publish(exchange='', routing_key='ckan2', body='ckan crawler exited')

    elif body == "bye ckan":
        channel.basic_publish(exchange='', routing_key='ckan2', body='connection closed')
        connection.close()

channel.basic_consume(callback,
                      queue='ckan',
                      no_ack=True)

print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()

