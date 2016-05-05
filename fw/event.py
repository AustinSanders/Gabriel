import copy

"""Events are the base unit of communication between ArchElements. They are currently realized as
plain ol', built-in python dictionaries. What makes them special is not *what* they are, but *how*
they are used and how their use is constrained.

Gabriel objects make only one assumption about Events: Events received from other objects are
immutable. The corollary is that events created by an object are only mutable until they are sent.
These assumptions exist for two reasons.
    1. As long as these assumptions hold invariably accross the system, Events can be passed by
       reference rather than by value. This results in a decent speed increase in systems with large
       quantities of events being passed around rapidly since events are not copied unless an object
       needs to mutate their contents. This means that the number of copies that are created out of
       necessity is the minimal number of copies which can be created.

    2. As long as these assumptions hold invariably accross the system, Events will behave as they
       would if they were passed by value. That is, once initially sent, they will never change, and
       so there is no worry that an Event held by an object will be invalidated by another object
       after it is received.
       
As such, the assumption of all utility methods in this module is that the event passed in as a
parameter is not owned by the caller. Therefore, a copy is made before any alterations are
performed."""

# Appends the source parameter to a list in the event stored under the key 'source'
def append_source(event, source):
    event_copy = copy(event)
    event_copy['source'] = source
    return event_copy

def copy(event):
    return copy.deepcopy(event)
