import fw

class Component(fw.ArchElement):

    def behavior(self):
        for dispatcher in self.event_dispatchers:
            dispatcher.dispatch_event()

    def __init__(self, id, passed_behavior=None):
        if passed_behavior is None:
            super().__init__(self, id, self.behavior)
        else:
            super().__init__(self, id, passed_behavior)

