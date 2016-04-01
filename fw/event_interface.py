class EventInterface(list):
    def __init__(self, id):
        list.__init__(self)
        self.id = id

    def add_event_listener(self, event_listener):
        self.append(event_listener)

    def remove_event_listener(self, event_listener):
        self.remove(event_listener)

    def fire_event(self, event):
        for listener in self:
            listener.fire_event(event.copy())
