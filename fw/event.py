import copy


class Event():
    """Events are the base unit of communication between ArchElements. Events have two interesting
    properties: they must only be mutated by their creator and must not be mutated after they are
    sent. This is because, to increase the speed of Event passing, Events are passed by reference
    rather than by value. As a result, any modification to an Event will be seen by *all*
    ArchElements that receive and act on the same Event after it is modified.

        Ex.
            Suppose that there are three ArchElements connected like this:

                           /-->ElemB
                    ElemA--|
                           \-->ElemC

            Suppose A creates an Event 'E' and then sends it to both B and C.
                    E = {payload : {source : 'A'
                                    value  : "cool"}

            Further suppose that B handles E before C and makes a changes it as follows:
                    E = {payload : {source : 'B'
                                    value  : "not so cool"}

            Now, when C handles E, it will see E WITH THE CHANGES OF B! This could lead to total
            component failure for C if it is depending on E's payload being simply "cool".

    This may seem terrible, but in truth it is great because it requires that events must only be
    copied when they are modified instead of every time they pass through a component.

    To facilitate this, the payload field is prefixed with an underscore, and access to it is
    provided by the payload() and payload_copy() methods. This will help remind developers to think
    about how they are using the payload and whether a copy needs to be made or not. Finally, the
    module level function make_from() facilitates the creation of a new Event from an old one."""

    def __init__(self, payload=None):
        self._payload = payload

    def __str__(self):
        return("\tSource:\t\t{0}\n\tPayload:\t{1}".format(self.source, self._payload))

    def payload(self):
        return self._payload

    def payload_copy(self):
        return copy.deepcopy(self._payload)

    def append_source(self, source):
        try:
            self._payload["source"]
        except KeyError:
            e = make_from(self)
            e._payload["source"] = [source]
            return e
        e = make_from(self)
        e._payload["source"].append(source)
        return e


def make_from(other_event):
    Event(other_event.payload_copy())
