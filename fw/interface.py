"""An EventInterface is a collection of EventListeners representing bundle of outgoing connections
from the ArchElement that owns it."""
class EventInterface(list):
    def __init__(self, id):
        list.__init__(self)
        self.id = id

    def add_event_listener(self, event_listener):
        self.append(event_listener)

    def get_event_listener(self, event_listener_id):
        for event_listener in self:
            if event_listener.id == event_listener_id:
                return event_listener

    def remove_event_listener(self, event_listener_id):
        self.remove(self.get_event_listener(event_listener_id))

    def fire_event(self, event):
        for listener in self:
            listener.fire_event(event)
