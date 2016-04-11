from queue import Queue


class EventDispatcher(Queue):
    def __init__(self, id, owner, blocking=False, timeout=None, throwing=False):
        Queue.__init__(self)
        self.id = id
        self.owner = owner
        self.blocking = blocking
        self.timeout = timeout
        self.throwing = throwing
        self.event_handlers = []

    def add_event_handler(self, event_handler):
        event_handler.owner = self.owner
        self.event_handlers.append(event_handler)

    def get_event_handler(self, event_handler_id):
        for handler in self.event_handlers:
            if handler.id == event_handler_id:
                return handler

    def remove_handler(self, event_handler_id):
        self.event_handlers.remove(self.get_event_handler(event_handler_id))

    def dispatch_event(self):
        try:
            current_event = self.get(blocking, self.timeout)
            self.task_done()
            for handler in self.event_handlers:
                handler.handle(current_event)
        except Empty:
            if self.raising:
                raise Empty
