# connector.py
# Special purpose component for managing communication
import fw

class Connector(fw.Component):

    def __init__(self, id, behavior=None):
        super().__init__(id, behavior)
