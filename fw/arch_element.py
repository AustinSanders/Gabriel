import threading
from queue import Queue
from fw import ArchEventDispatcher


class ArchElement(threading.Thread):

    def __init__(self, id, passed_behavior=None):
        threading.Thread.__init__()
        self.id = id
        self.interfaces = []
        self.event_dispatchers = []
        self.properties = {}
        self.elem_status = "INITIATED"
        self.behavior = passed_behavior

    # This class method should return a dictionary after the pattern of the one provided here. It is
    # used by the ArchitectureManager to make connections between ArchElements.
    def description():
        description = {           "name" : "ArchElement",
                                 "class" : ArchElement,
                            "interfaces" : [], # List of strings which are interface ids
                           "dispatchers" : [], # List of strings which are dispatcher ids
                        "events_emitted" : [], # List of classes which subclass Event
                       "events_consumed" : [], # List of classes which subclass Event
                      }
        raise NotImplementedError

    # Override this method with a callable which implements how this ArchElement's thread will
    # behave. This method is called in a loop by the run method and, as such, should not include any
    # infinite loops or waiting without a timeout.
    def behavior(self):
        raise NotImplementedError

    # DO NOT OVERRIDE:
    #   To specify behavior for an ArchElement override its 'behavior' method.
    # Check for any ArchEvents and do the appropriate thing based on the event type. Then run
    # behavior.
    def run(self):
        while true:
            self.arch_event_dispatcher.dispatch_event()
            if self.elem_status == "STOPPED":
                break
            elif self.elem_status == "SUSPENDED":
                pass
            elif self.elem_status == "RUNNING":
                self.behavior()

    # Temporarily suspends the ArchElement's activity if it was running, otherwise it should do
    # nothing. Undone by calling resume().
    def suspend(self):
        self.elem_status == "SUSPENDED"

    # Resumes the ArchElement's activity if it was suspended, otherwise it should do nothing.
    # NOTE: Suspended ArchElements should still receive events
    def resume(self):
        self.elem_status == "RUNNING"

    # Prepares the ArchElement for deletion or removal from the architecture and halts its activity.
    # NOTE: Stopped ArchElements should ignore events received after they are stopped
    def stop(self):
        self.elem_status == "STOPPED"

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

