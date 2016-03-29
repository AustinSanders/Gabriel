class Component(ArchElement):

    def __init__(self, id, behavior=None):
        super().__init__(self, id, behavior)

    #@TODO replace "Component" with class name?
    def __str__(self):
        return "Component: " + str(self._id)

##@TODO## addRawMessageListener:

##@TODO## removeRawMessageListener:

#TODO:
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
