class EventListener:

    def __init__(self, id, event_store):
        assert (event_store is not None)

        self.id = id
        self.event_store = event_store

    def fire_event(self, event):
        self.event_store.put(event)
