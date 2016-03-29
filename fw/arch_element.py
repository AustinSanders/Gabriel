import threading
from queue import Queue


class ArchElement(threading.Thread):

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
        if behavior is not None:
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

    @property
    def send_to_all_handler(self, handler):
        """Sets the send to all handler for this component"""
        self._send_to_all_handler = handler

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
        print("Event listener ", listener, "not found / removed on connector ", self._id)

    def add_bottom(self, listener):
        """Adds an event listener to the bottom port."""
        if (listener in self._bottom):
            self._bottom.remove(listener)
            return
        print("Event listener ", listener, " not found / removed on connector ", self._id)

    def property_names(self):
        """Returns a list of the names of all properties in the element"""
        prop_names = []
        for key in self._properties:
            prop_names.append(key)
        return prop_names

    def update_property(self, prop, value):
        prop = str(prop)
        self._properties[prop] = value

    def remove_property(self, prop):
        """Delete the specified property from the table if it exists"""
        prop = str(prop)
        if(prop in self._properties):
            del self._properties[prop]
        print('No property found with name ' + prop)

    def request(self, message):
        """Sends the specified message to all components listening to its top port"""
        assert (message.source is not None)
        for i in self._top:
            c_message = message.copy()
            i.requests.put(c_message.append_sender(self))

    def notify(self, message):
        """Sends the specified message to all components listening to its bottom port"""
        assert (message.source is not None)
        for i in self._bottom:
            c_message = message.copy()
            i.notifications.put(c_message.append_sender(self))

# @TODO Implement interface style functions (i.e. raise NotImplementedErrors)
