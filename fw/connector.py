
# connector.py
# Special purpose component for managing communication

from . import component

class Connector(component.Component):


    def __init__(self,id):
        super().__init__(id)
        self._top = []
        self._bottom = []


    def add_top(self, listener):
        """Adds an event listener to the top port."""
        self._top.append(listener)


    def remove_top(self, listener):
        """Removes the specified event listener from the top port."""
        if (listener in self._top):
            self._top.remove(listener)
            return
        print("Event listener ", listener," not found / removed on connector ", self._id)


    def add_bottom(self, listener):
        """Adds an event listener to the bottom port."""
        self._bottom.append(listener)


    def remove_bottom(self, listener):
        """Removes the specified listener from the bottom port."""
        if (listener in self._bottom):
            self._bottom.remove(listener)
            return
        print("Event listener ", listener," not found / removed on connector ", self._id)

    def request(self, message):
        """Sends the specified message to all components listening to its top
        port"""
        assert ( message.source is not None )
        for i in self._top:
            c_message = message.copy()
            c_message.source.append(self.id)
            i.request_sent(c_message)

    def notify(self, message):
        """Sends the specified message to all components listening to its
        bottom port"""
        assert ( message.source is not None )
        if self.id in message.source:
            return
        for i in self._bottom:
            c_message = message.copy()
            c_message.source.append(self.id)
            i.message_send(c_message)


