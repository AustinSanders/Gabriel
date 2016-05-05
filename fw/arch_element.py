import threading
from queue import Queue
from fw import ArchEventDispatcher


class ArchElement(threading.Thread):

    def __init__(self, id, behavior=None):
        threading.Thread.__init__()
        self.id = id
        self.interfaces = []
        self.dispatchers = []
        self.elem_status = "INITIATED"
        self.behavior = behavior

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
        if self.elem_status == "INITIATED":
            self.elem_status = "RUNNING"
        while true:
            if self.elem_status == "STOPPED":
                break
            elif self.elem_status == "SUSPENDED":
                pass # TODO: Figure this out
            elif self.elem_status == "RUNNING":
                self.behavior()

    # Temporarily suspends the ArchElement's activity if it was running, otherwise it should do
    # nothing. Undone by calling resume().
    def suspend(self):
        if self.elem_status == "RUNNING":
            self.elem_status == "SUSPENDED"

    # Resumes the ArchElement's activity if it was suspended, otherwise it should do nothing.
    # NOTE: Suspended ArchElements should still receive events
    def resume(self):
        if self.elem_status == "SUSPENDED":
            self.elem_status == "RUNNING"
        else:
            raise RuntimeError

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

    def add_dispatcher(self, dispatcher):
        self.dispatchers.append(dispatcher)

    def get_dispatcher(self, dispatcher_id):
        for dispatcher in self.dispatchers:
            if dispatcher.id == dispatcher_id:
                return dispatcher
        return None

    def remove_dispatcher(self, dispatcher_id):
        self.dispatchers.remove(self.get_dispatcher(id))

    def fire_event_on_interface(self, event, interface_id):
        self.get_interface(interface_id).fire_event(event.append_source(self.id))

    def fire_event_on_all_interfaces(self, event):
        for interface in self.interfaces:
            interface.fire_event(event)

    def __str__(self):
        return "{0}: {1}".format(self.__class__.__name__, self.id)

    def type(self):
        """Return the class name of the architectural element"""
        return self.__class__.__name__

