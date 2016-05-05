from queue import Queue

"""An EventDispatcher is a container for inbound Events and a collection of Event_Handlers that are
will be allowed a chance to handle all received events serially. They have an unique identifier for
locating them within containers. They are also constructed with three configuration parameters:
blocking, timeout, and throwing. Respectively, these determine whether the event dispatcher will
block when it is waiting to fetch an event from its internal Queue; what timeout (in TODO: units) to
wait when trying to fetch an event, and whether or not the dispatcher will throw or silently swallow
the Empty exception that is thrown when an attempt is made to dispatch an event and none are
available."""
class EventDispatcher(Queue):
    def __init__(self, id, blocking=False, timeout=None, throwing=False):
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

    def fire_event(self, event):
        self.put(event)

    def dispatch_event(self):
        try:
            current_event = self.get(blocking, self.timeout)
            self.task_done()
            for handler in self.event_handlers:
                handler.handle(current_event)
        except Empty:
            if self.raising:
                raise Empty
