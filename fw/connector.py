# connector.py
# Special purpose component for managing communication
class Connector(Component):


    def __init__(self, id, behavior=None):
        def basic_connector():
            pass

        if behavior is not None:
            super().__init__(id, behavior)
        else:
            super().__init__(id, basic_connector)
