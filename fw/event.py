import copy

class Event():
    def __init__(self, payload=None):
        self.source = []
        self.payload = payload
        self.context = {}

    def __str__(self):
        return("\tSource:\t\t{0}\n\tPayload:\t{1}".format(self.source, self.payload))

    def copy(self):
        cp = Event()
        cp.source = copy.deepcopy(self.source)
        cp.payload = copy.deepcopy(self.payload)
        cp.context = copy.copy(self.context)
        return cp

    def append_source(self, source):
        if source != None:
            self.source.append(source)
        return self
