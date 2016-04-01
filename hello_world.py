import fw
import time
from queue import Empty

def connect(interface_pair, dispatcher_pair, id):
    el = fw.EventListener(id, dispatcher_pair[0].get_event_dispatcher(dispatcher_pair[1]))
    interface_pair[0].get_interface(interface_pair[1]).add_event_listener(el)

class Sender(fw.Component):

    class NotificationHandler():
        def handle(self, event):
            print("{0} received a notification:\n{1}\n".format(event.context["owner"], event))

    def sender_behavior(self):
        notifications_received = 0
        while notifications_received < 32:
            for dispatcher in self.event_dispatchers:
                try:
                    dispatcher.dispatch_event()
                    notifications_received += 1
                except Empty:
                    pass

            if notifications_received % 2 == 0:
                e = fw.Event("Message: {0}".format(notifications_received / 2))
                self.fire_event_on_interface(e, "top")
                time.sleep(.5)

    def __init__(self, id):
        super().__init__(id, self.sender_behavior)
        self.add_interface(fw.EventInterface("top"))
        from_top = fw.EventDispatcher("notification_dispatcher", self, 0.1)
        from_top.add_event_handler(self.NotificationHandler())
        self.add_event_dispatcher(from_top)
        self.properties["owner"] = self


class Receiver(fw.Component):

    class RequestHandler():
        def handle(self, event):
            print("{0} received a request:\n{1}\n".format(event.context["owner"], event))
            event.context["owner"].fire_event_on_interface(event, "bottom")

    def receiver_behavior(self):
        received_messages = 0
        while received_messages < 16:
            for dispatcher in self.event_dispatchers:
                try:
                    dispatcher.dispatch_event()
                    received_messages += 1
                except Empty:
                    pass

    def __init__(self, id):
        super().__init__(id, self.receiver_behavior)
        self.add_interface(fw.EventInterface("bottom"))
        from_bot = fw.EventDispatcher("request_dispatcher", self, 0.1)
        from_bot.add_event_handler(self.RequestHandler())
        self.add_event_dispatcher(from_bot)
        self.properties["owner"] = self


class Router(fw.Connector):

    class NotificationHandler():
        def handle(self, event):
            event.context["owner"].fire_event_on_interface(event, "bottom")

    class RequestHandler():
        def handle(self, event):
            event.context["owner"].fire_event_on_interface(event, "top")

    def router_behavior(self):
        forwarded_notifications = 0
        while forwarded_notifications < 32:
            for dispatcher in self.event_dispatchers:
                try:
                    dispatcher.dispatch_event()
                    if dispatcher.id == "notification_dispatcher":
                        forwarded_notifications += 1
                except Empty:
                    pass

    def __init__(self, id):
        super().__init__(id, self.router_behavior)
        self.add_interface(fw.EventInterface("top"))
        self.add_interface(fw.EventInterface("bottom"))
        from_top = fw.EventDispatcher("notification_dispatcher", self, 0.1)
        from_top.add_event_handler(self.NotificationHandler())
        self.add_event_dispatcher(from_top)
        from_bot = fw.EventDispatcher("request_dispatcher", self, 0.1)
        from_bot.add_event_handler(self.RequestHandler())
        self.add_event_dispatcher(from_bot)
        self.properties["owner"] = self



if __name__ == "__main__":
    sender = Sender("Sender")
    router = Router("Router")
    receiver_a = Receiver("Receiver_A")
    receiver_b = Receiver("Receiver_B")

    connect((sender, "top"), (router, "request_dispatcher"), 0)

    connect((router, "bottom"), (sender,     "notification_dispatcher"), 0)
    connect((router, "top"),    (receiver_a, "request_dispatcher"), 0)
    connect((router, "top"),    (receiver_b, "request_dispatcher"), 1)

    connect((receiver_a, "bottom"), (router, "notification_dispatcher"), 0)
    connect((receiver_b, "bottom"), (router, "notification_dispatcher"), 0)

    receiver_a.start()
    receiver_b.start()
    router.start()
    sender.start()

    receiver_a.join()
    receiver_b.join()
    router.join()
    sender.join()
