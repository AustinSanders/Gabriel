
# Brick.py
# Superclass for components and connectors

class Brick():

#   Unique identifier for this brick
    _id = None


#   Message Listeners
    _message_listeners = []


#   Architectural Message Listener
    _arch_message_listeners = []


#   Property table for this brick
    _properties = None


#   Send to all handler for this brick
    _send_to_all_handler = None


    def __init__(self, id):
        self._id = id
        self._properties = {}


    def __str__(self):
        return "Brick: " + str(self._id)


    @property
    def id(self):
        """Getter for unique ID"""
        return self._id


    @property
    def properties(self):
        """Getter for properties"""
        return self._properties


    def type(self):
        """Returns the class name as a string"""
        return self.__class__.__name__


    # @TODO synchronize?
    def add_message_listener(self, message_listener):
        """Adds the specified message listener to the brick's list of message listeners"""
        if message_listener not in  self._message_listeners:
            self._message_listeners.append(message_listener)
            return 1
        return 0


    def remove_message_listener(self, message_listener):
        """Removes the specified message listener from the brick's list of message listeners"""
        if message_listener in self._message_listeners:
            self._message_listeners.remove(message_listener)
            return 1
        return 0


##@TODO## addRawMessageListener:

##@TODO## removeRawMessageListener:

    def send(self, message):
        """Sends the specified message to all message listeners."""
        assert ( message.destination is not None and message.source is not None )
        for i in self._message_listeners:
            i.message_sent(message)


    @property
    def send_to_all_handler(self, handler):
        """Sets the send to all handler for this brick"""
        self._send_to_all_handler = handler
        return 1

##### sendToAll
#    def send_to_all(self, message, interface):
#        """Sends the specified message to all bricks welded to the specified interface"""
#       assert (message is not None and interface is not None)
#      idp = interface.interface_id_pair
#     destinations = interface.get_all_connected_intefaces()
#
#TODO:
##### getAllIntefaceIDs:
##### hasInterface:

    def set_property(self, prop, value):
        self._properties[prop] = value

    def property_names(self):
        '''Returns a list of the names of all properties in the brick'''
        prop_names = []
        for key in self._properties:
            prop_names.append(key)
        return prop_names

    def remove_property(self, prop):
        '''Delete the specified property from the table if it exists'''
        if(prop in self._properties):
            del self._properties[prop]
            return 1
        return 0

##### addArchMessageListener
##### removeArchMessageListener
##### sendArchMessage

#(implemented by child class
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



