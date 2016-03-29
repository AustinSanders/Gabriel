import threading
from queue import Queue

class Arch_Element(threading.Thread):

    def __init__(self, id, behavior=None):
        super().__init__(self)
        self._top = []
        self._bottom = []
        self._arch_message_listeners = []
        self._send_to_all_handler = None
        self._id = id
        self._properties = {}
        self.requests = Queue()
        self.notifications = Queue()
        if behavior != None:
            self.run = behavior


    def __str__(self):
        return "Element: " + str(self._id)


    @property
    def id(self):
        """Getter for unique ID"""
        return self._id


    @property
    def properties(self):
        """Getter for properties"""
        return self._properties


    @property
    def top(self):
        """Getter for top port"""
        return self._top


    @property
    def bottom(self):
        """Getter for bottom port"""
        return self._bottom


    def type(self):
        """Return the class name of the architectural element"""
        return self.__class__.__name__


    def add_top(self, listener):
        """Adds an event listener to the top port"""
        self._top.append(listener)


    def remove_top(self, listener):
        """Remove an event listener from the top port"""
        if (listener in self._top):
            self._top.remove(listener)
            return
        print("Event listener ", listener,"not found / removed on connector ", self._id)


    def add_bottom(self, listener):
        """Adds an event listener to the bottom port."""
        if (listener in self._bottom):
            self._bottom.remove(listener)
            return
        print("Event listener ", listener," not found / removed on connector ", self._id)


    def request(self, message):
        """Sends the specified message to all components listening to its top port"""
        assert ( message.source is not None )
        for i in self._top:
            c_message = message.copy()
            i.requests.put(c_message.append_sender(self))


    def notify(self, message):
        """Sends the specified message to all components listening to its bottom port"""
        assert ( message.source is not None )
        for i in self._bottom:
            c_message = message.copy()
            i.notifications.put(c_message.append_sender(self))

#@TODO Implement interface style functions (i.e. raise NotImplementedErrors)
