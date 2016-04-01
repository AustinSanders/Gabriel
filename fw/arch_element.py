import threading


class ArchElement(threading.Thread):

    def __init__(self, id, behavior=None):
        super().__init__(target=behavior)
        self.id = id
        self.interfaces = []
        self.event_dispatchers = []
        self.properties = {}

    def add_interface(self, interface):
        self.interfaces.append(interface)

    def get_interface(self, interface_id):
        for interface in self.interfaces:
            if interface.id == interface_id:
                return interface
        return None

    def remove_interface(self, interface_id):
        self.interfaces.remove(self.get_interface(interface_id))

    def add_event_dispatcher(self, event_dispatcher):
        self.event_dispatchers.append(event_dispatcher)

    def get_event_dispatcher(self, event_dispatcher_id):
        for dispatcher in self.event_dispatchers:
            if dispatcher.id == event_dispatcher_id:
                return dispatcher
        return None

    def remove_event_dispatcher(self, event_dispatcher_id):
        self.event_dispatchers.remove(self.get_event_dispatcher(id))

    def fire_event_on_interface(self, event, interface_id):
        self.get_interface(interface_id).fire_event(event.append_source(self.id))

    def broadcast_event(self, event):
        for interface in self.interfaces:
            interface.fire_event(event)

    def property_names(self):
        """Returns a list of the names of all properties in the element"""
        prop_names = []
        for key in self.properties:
            prop_names.append(key)
        return prop_names

    def add_or_update_property(self, prop, value):
        prop = str(prop)
        self.properties[prop] = value

    def remove_property(self, prop):
        """Delete the specified property from the table if it exists"""
        prop = str(prop)
        if(prop in self.properties):
            del self.properties[prop]
        else:
            print('No property found with name ' + prop)

    def __str__(self):
        return "{0}: {1}".format(self.__class__.__name__, self.id)

    def type(self):
        """Return the class name of the architectural element"""
        return self.__class__.__name__

