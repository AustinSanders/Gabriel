import copy

class Message():
    def __init__(self, source=None, payload=None):
        self.source = []
        if source != None:
            self.source.append(source)

        self.payload = payload


    def __str__(self):
        return("Source path\t" + str(self.source) + "\n" +
               "\tPayload\t" + str(self.payload))


    def copy(self):
        return copy.deepcopy(self)
