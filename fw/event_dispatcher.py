from queue import Queue


class EventDispatcher(Queue):
    def __init__(self, id, owner, timeout=None):
        Queue.__init__(self)
        self.id = id
        self.owner = owner
        self.timeout = timeout
        self.event_handlers = []

    def add_event_handler(self, event_handler):
        self.event_handlers.append(event_handler)

    def get_event_handler(self, event_handler_id):
        for handler in self.event_handlers:
            if handler.id == event_handler_id:
                return handler

    def remove_handler(self, event_handler_id):
        self.event_handlers.remove(self.get_event_handler(event_handler_id))

    # Throws queue.Empty exception
    def dispatch_event(self):
        current_event = self.get(True, self.timeout).copy()
        current_event.context = self.owner.properties
        for handler in self.event_handlers:
            handler.handle(current_event)
