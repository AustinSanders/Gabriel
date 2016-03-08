import fw.brick

class Message():

    _source = None
    _destination = None
    _body = None

    def __init__(self, source, destination, body):
        self._source = source
        self._destination = destination
        self._body = body

    @property
    def destination(self):
        return self._destination

    @property
    def source(self):
        return self._source

    @property
    def body(self):
        return self._body
    
        

class Sender(fw.brick.Brick):

    def __init__(self, id):
        super().__init__(id)


class Receiver(fw.brick.Brick):

    def __init__(self,id):
        super().__init__(id)

    def message_sent(self, message):
        print("component " , self.id , " received message : \n\t" +
              message.body)


t_sender = Sender(1)
t_receiver = Receiver(2)

t_sender.add_message_listener(t_receiver)

m = Message(t_sender.id, t_receiver.id, "Hello World!")

t_sender.send(m)

print(t_sender.id)
print(t_receiver.id)
