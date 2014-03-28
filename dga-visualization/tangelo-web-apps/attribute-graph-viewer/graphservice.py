import tangelo
import os
import csv

allowed = ['list','get']


def run(operation, name=''):
  print 'in run with operation ',operation
  if ('list' == operation): return listGraphs()
  if ('get' == operation) :return getGraph(name)
  
  
def listGraphs():
  return  os.listdir('graphs')
  
def getGraph(name):
  if os.path.exists('graphs/'+name):
    return readGraph(name)
  else:
    raise ValueError("no graph named "+name)
  
  
def readGraph(name):
  fobj = open('graphs/'+name+'/edges.csv')
  reader = csv.reader(fobj)
  nodes = []
  edges = []  
  curr_node = 0
  node_map  = {}
  groups = {}
  curr_group = 0
  
  for edge in reader:
    for vertex in edge:
      if vertex not in node_map:
        type = vertex[:vertex.index(':')]
        if type not in groups:
          groups[type] = curr_group
          curr_group +=1
        group = groups[type]
        node = {'name':vertex,'group':group,'index':curr_node} 
        nodes.append(node)
        node_map[vertex] = curr_node
        curr_node += 1
    value = 1
    if len(edge) > 2: value = edge[2] 
    edges.append({"source":node_map[edge[0]],'target':node_map[edge[1]],'value':value})
    
  graph = {'nodes':nodes, 'links':edges}
  return graph  
    
      
    
    
  
