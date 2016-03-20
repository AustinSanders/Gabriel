
# component.py
# Superclass for components and connectors

import multiprocessing
class Component(multiprocessing.Process):

    def __init__(self, id):
        super().__init__()
        self._top = None #port for sending requests
        self._bottom = None #port for sending notifications
        self._arch_message_listeners = [] #port for sending architectural messages
        self._send_to_all_handler = None
        self._id = id #unique identifier for this component
        self._properties = {} #table holding properties of the component


    #@TODO replace "Component" with class name?
    def __str__(self):
        return "Component: " + str(self._id)


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
        """Getter for top interface"""
        return self._top

    @property
    def bottom(self):
        """Getter for bottom interface"""
        return self._bottom

    def type(self):
        """Returns the class name as a string"""
        return self.__class__.__name__


    def add_top(self, listener):
        """Welds the event listener to the top port."""
        if self._top == None:
            self._top = listener

    def remove_top(self):
        """Removes the event listener from the top port."""
        self._top = None

    def add_bottom(self, listener):
        """Welds the event listener to the bottom port."""
        if self._bottom == None:
            self._bottom = listener

    def remove_bottom(self):
        """Removes the event listener from the bottom port."""
        self._bottom = None

##@TODO## addRawMessageListener:

##@TODO## removeRawMessageListener:

    def request(self, message):
        """Sends the specified message to the components listening to its top
        port"""
        assert ( message.destination is not None and message.source is not None )
        c_message = message.copy()
        c_message.source.append(self.id)
        self.top.request_sent(c_message)

    def notify(self, message):
        """Sends the specified message to the component connected to the
        bottom port"""
        assert ( message.destination is not None and message.source is not None )
        c_message = message.copy()
        c_message.source.append(self.id)
        self.bottom.notification_sent(c_message)

    @property
    def send_to_all_handler(self, handler):
        """Sets the send to all handler for this component"""
        self._send_to_all_handler = handler

##### sendToAll
#    def send_to_all(self, message, interface):
#        """Sends the specified message to all components welded to the specified interface"""
#       assert (message is not None and interface is not None)
#      idp = interface.interface_id_pair
#     destinations = interface.get_all_connected_intefaces()
#
#TODO:
##### getAllIntefaceIDs:
##### hasInterface:

    def set_property(self, prop, value):
        prop = str(prop)
        self._properties[prop] = value

    def property_names(self):
        '''Returns a list of the names of all properties in the component'''
        prop_names = []
        for key in self._properties:
            prop_names.append(key)
        return prop_names

    def remove_property(self, prop):
        '''Delete the specified property from the table if it exists'''
        prop = str(prop)
        if(prop in self._properties):
            del self._properties[prop]
        print('No property found with name ' + prop)

##### addArchMessageListener
##### removeArchMessageListener
##### sendArchMessage

#(implemented by child class)
##### getInterface
    def get_interface(id):
        raise NotImplementedError('Get_interface(id) must be overridden by child class')

##### getAllInterfaces
    def get_all_interfaces():
        raise NotImplementedError('get_all_interfaces() must be overriden by child class')

#### init

##### begin
    def begin():
        raise NotImplementedError('begin() must be overridden by child class')

##### end
    def end():
        raise NotImplementedError('end() must be overridden by child class')

##### destroy
    def destroy():
        raise NotImplementedError('destroy() must be overridden by child class')


##### handle
    def handle():
        raise NotImplementedError('handle() must e overridden by child class')


#(thread related methods to be overridden by child class)
##### start

##### suspend
##### resume
##### stop



