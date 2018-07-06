#!/usr/bin/env python
import pika
import re
import main
import exceptions



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
            # print(result[0]) #a
            # print(result[1]) #b
            # print(result[2]) #c
            if(result[0] == 0):
                print(result[0])
                channel.basic_publish(exchange='', routing_key='ckan2', body='finished dumping')
            elif(result[0] == 1):
                print(result[0])
                channel.basic_publish(exchange='', routing_key='ckan2', body='Error# error processing ckan url')
                if(result[2] == 2):
                    print(result[2])
                    channel.basic_publish(exchange='', routing_key='ckan2', body='Error# IOError')
                    if(result[1] == 3):
                        print(result[1])
                        channel.basic_publish(exchange='', routing_key='ckan2', body='ckan crawler exited')


        print(result)

channel.basic_consume(callback,
                      queue='ckan',
                      no_ack=True)

print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()

