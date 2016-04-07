class ArchEvent(fw.Event):

    """An event sent by the ArchitectureManager containing commands to be carried out by components,
    specifically commands to start, stop, suspend, and resume activity."""

    formal_types = ["START",
                    "STOP",
                    "SUSPEND",
                    "RESUME",]

    def __init__(self, type):
        """TODO: to be defined1. """
        formal_type = type.upper()

        if formal_type not in formal_types:
            raise LookupError

        fw.Event.__init__(self, formal_type)

