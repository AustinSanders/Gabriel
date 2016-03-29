import copy

class Message():
    def __init__(self, payload=None):
        self.source = []
        self.payload = payload


    def __str__(self):
        return("Source path\t" + str(self.source) + "\n" +
               "\tPayload\t" + str(self.payload))


    def copy(self):
        return copy.deepcopy(self)

    def append_sender(self, sender):
        if sender != None:
            self.source.append(sender)
        return self
