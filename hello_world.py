from fw import *
import time
import random
import multiprocessing as mp

class Sender(component.Component):

    def __init__(self, id):
        super().__init__(id)

    def run(self):
        for i in range(20):
            m = message.Message(self.id, str(random.randrange(3,5)), str(i))
            self.request(m)
            time.sleep(.5)


class Router(connector.Connector):


    def __init__(self, id):
        super().__init__(id)
        self.active = mp.Value('b', False)

    def run(self):
        print("Router activated")
        self.active.value = True

    def notification_sent(self, message):
        if self.active.value == True:
            self.notify(message)
        else:
            print("Message rejected because component activity is " , self.active.value)


    def request_sent(self, message):
        if self.active.value == True:
            self.request(message)
        else:
            print("Message rejected because component activity is " , self.active.value)


class Receiver(component.Component):

    def __init__(self,id):
        super().__init__(id)

    def request_sent(self, message):
       if (self.id in message.destination):
            print("component " , self.id , " received message : \n\t" +
                  str(message))

    def notification_sent(self, message):
        if (self.id in message.destination):
            print("component " , self.id , " received notification : \n\t" +
                    str(message))


    #??
    def run(self):
        pass



if __name__ == "__main__":

    t_sender = Sender(1)
    t_router = Router(2)
    t_receiver = Receiver(3)
    s_receiver = Receiver(4)

    t_sender.add_top(t_router)
    t_router.add_bottom(t_sender)

    t_router.add_top(t_receiver)
    t_receiver.add_bottom(t_router)

    t_router.add_top(s_receiver)
    s_receiver.add_bottom(t_router)

    t_sender.start()
    t_receiver.start()
    s_receiver.start()
    time.sleep(2)
    t_router.start()


    t_router.join
    t_sender.join()
    t_receiver.join()
    s_receiver.join()

    print(t_sender.id , t_sender.pid)
    print(t_router.id , t_router.pid)
    print(t_receiver.id, t_receiver.pid)
    print(s_receiver.id, s_receiver.pid)
