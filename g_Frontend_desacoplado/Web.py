import requests
from flask import Flask, render_template, redirect, url_for, flash, request
from forms import FormularioEmpresa, FormularioProyecto, FormularioRequisito, FormularioDiagrama

app = Flask(__name__)
app.config['SECRET_KEY'] = 'clave_secreta_super_segura'
JAVA_API_URL = "http://localhost:8080/api/v1"

# --- HERRAMIENTA PARA EXTRAER ID DE LAS URLS DE SPRING ---
def obtener_id_de_url(url):
    """Ejemplo: de '.../empresas/5' saca el '5'"""
    try:
        if not url: return "0"
        return url.split("/")[-1]
    except:
        return "0"

# --- 1. RUTAS DE INICIO Y EMPRESAS ---
@app.route('/')
def index():
    return render_template('index.html')

@app.route('/empresas')
def ver_empresas():
    # 1. Obtener parámetros
    pagina = request.args.get('page', default=0, type=int)
    tamanio = request.args.get('size', default=5, type=int)  # Puedes cambiar el default

    # 2. Llamar a Java CON paginación
    url_api = f'http://localhost:8080/api/empresas?page={pagina}&size={tamanio}'

    try:
        respuesta = requests.get(url_api)
        if respuesta.status_code == 200:
            datos = respuesta.json()

            # Si Java envía un objeto Page
            if 'content' in datos:
                # Procesar los IDs si vienen dentro de '_links' (HATEOAS)
                contenido_procesado = []
                for empresa in datos['content']:
                    # Extraer ID si está en los enlaces (ajusta el nombre de la clave si es necesario)
                    if '_links' in empresa and 'self' in empresa['_links']:
                        self_url = empresa["_links"]["self"]["href"]
                        empresa["id"] = self_url.split('/')[-1]  # Extrae el ID final de la URL
                    contenido_procesado.append(empresa)
                datos['content'] = contenido_procesado
                datos_paginados = datos

            # Si Java envía una lista simple (fallback)
            elif isinstance(datos, list):
                datos_paginados = {
                    "content": datos,
                    "first": True,
                    "last": True,
                    "number": 0,
                    "totalPages": 1,
                    "totalElements": len(datos)
                }
            else:
                datos_paginados = {"content": [], "totalPages": 0, "number": 0}
        else:
            datos_paginados = {"content": [], "totalPages": 0, "number": 0}
    except Exception as e:
        print(f"Error crítico en /empresas: {e}")
        datos_paginados = {"content": [], "totalPages": 0, "number": 0}

    # 3. Pasar los datos procesados a la plantilla
    return render_template('lista_empresas.html', paginacion=datos_paginados)

@app.route('/empresas/nueva', methods=['GET', 'POST'])
def crear_empresa():
    form = FormularioEmpresa()
    if form.validate_on_submit():
        json_data = { "nombre": form.nombre.data, "activo": form.activo.data }
        try:
            requests.post(f"{JAVA_API_URL}/empresas", json=json_data)
            return redirect(url_for('ver_empresas'))
        except:
            flash("Error al crear empresa")
    return render_template('form_generico.html', form=form, titulo="Nueva Empresa")

@app.route('/empresas/editar/<id>', methods=['GET', 'POST'])
def editar_empresa(id):
    # URL de tu API Java
    url_api = f'http://localhost:8080/api/empresas/{id}'

    if request.method == 'POST':
        # --- PARTE 2: GUARDAR LOS CAMBIOS ---
        
        # Obtenemos los datos del HTML
        nombre = request.form['nombre']
        activo = 'activo' in request.form # Checkbox: True si está, False si no
        
        # Preparamos el JSON para Java
        datos_actualizar = {
            "idEmpresa": id,
            "nombre": nombre,
            "activo": activo
        }
        
        # Enviamos el PUT a Java
        headers = {'Content-Type': 'application/json'}
        respuesta = requests.put(url_api, json=datos_actualizar, headers=headers)
        
        if respuesta.status_code == 200 or respuesta.status_code == 204:
            return redirect('/empresas') # Éxito, volver a la lista
        else:
            return f"Error al actualizar: {respuesta.text}"

    # --- PARTE 1: MOSTRAR EL FORMULARIO ---
    
    # Pedimos los datos actuales a Java (GET)
    respuesta = requests.get(url_api)
    
    if respuesta.status_code == 200:
        datos_empresa = respuesta.json()
        return render_template('editar_empresa.html', empresa=datos_empresa)
    else:
        return "Error: No se pudo cargar la empresa para editar."
    
@app.route('/empresas/eliminar/<id>')
def eliminar_empresa(id):
    # 1. URL de la API Java para esa empresa específica
    url_api = f'http://localhost:8080/api/empresas/{id}'
    
    # 2. Enviamos la orden DELETE a Java
    # No necesitamos enviar JSON, solo la petición
    respuesta = requests.delete(url_api)
    
    # 3. Verificamos el resultado
    if respuesta.status_code == 200 or respuesta.status_code == 204:
        # Éxito: Recargamos la lista para que veas que ya no está
        return redirect('/empresas')
    else:
        # Error: Mostramos qué pasó
        return f"Error al eliminar. Java respondió: {respuesta.status_code} - {respuesta.text}"    

# --- 2. RUTAS DE PROYECTOS (AQUÍ ESTÁ LA MAGIA) ---

@app.route('/proyectos')
def ver_proyectos():
    # 1. Obtener número de página
    pagina = request.args.get('page', default=0, type=int)
    tamanio = request.args.get('size', default=5, type=int)

    # 2. Pedir a Java la página de proyectos
    url_api = f'http://localhost:8080/api/proyectos?page={pagina}&size={tamanio}'

    try:
        respuesta = requests.get(url_api)
        if respuesta.status_code == 200:
            datos = respuesta.json()

            # 3. Si Java devuelve un objeto Page (con 'content')
            if 'content' in datos:
                contenido = datos.get('content', [])
                lista_final = []

                for proyecto in contenido:
                    # 4. Extraer el ID del proyecto (si no viene, usar el campo 'idProyecto' o 'id')
                    if 'id' not in proyecto:
                        # Intentar con diferentes nombres de campo
                        if 'idProyecto' in proyecto:
                            proyecto['id'] = proyecto['idProyecto']
                        elif 'proyectoId' in proyecto:
                            proyecto['id'] = proyecto['proyectoId']
                        else:
                            # Si no hay, intentar extraer de _links (por si acaso)
                            if '_links' in proyecto and 'self' in proyecto['_links']:
                                self_url = proyecto["_links"]["self"]["href"]
                                proyecto['id'] = obtener_id_de_url(self_url)

                    # 5. Extraer información de la empresa
                    # Asumimos que el campo 'empresa' está presente en el JSON
                    if 'empresa' in proyecto and proyecto['empresa']:
                        empresa = proyecto['empresa']
                        proyecto['empresa_id_real'] = empresa.get('idEmpresa', empresa.get('id', 'N/A'))
                        proyecto['empresa_nombre_real'] = empresa.get('nombre', 'Desconocido')
                    else:
                        proyecto['empresa_id_real'] = 'Sin Asignar'
                        proyecto['empresa_nombre_real'] = 'Desconocido'

                    lista_final.append(proyecto)

                # Reemplazar el contenido con la lista procesada
                datos['content'] = lista_final
                datos_paginados = datos

            # 6. Si Java devuelve una lista simple (por si acaso)
            elif isinstance(datos, list):
                # Procesar cada proyecto en la lista (similar a arriba)
                lista_final = []
                for proyecto in datos:
                    # ... mismo procesamiento que arriba ...
                    pass
                datos_paginados = {
                    "content": lista_final,
                    "first": True,
                    "last": True,
                    "number": pagina,
                    "totalPages": 1,
                    "totalElements": len(lista_final),
                    "size": tamanio,
                    "numberOfElements": len(lista_final)
                }
            else:
                datos_paginados = {"content": [], "totalPages": 0, "number": pagina}

        else:
            print(f"Error API Java (proyectos): {respuesta.status_code}")
            datos_paginados = {"content": [], "totalPages": 0, "number": pagina}

    except Exception as e:
        print(f"Error conectando a API Java (proyectos): {e}")
        datos_paginados = {"content": [], "totalPages": 0, "number": pagina}

    # 7. Pasar a la plantilla
    return render_template('lista_proyectos.html', paginacion=datos_paginados)
@app.route('/proyectos/nuevo', methods=['GET', 'POST'])
def crear_proyecto():
    form = FormularioProyecto()
    
    if form.validate_on_submit():
        # --- CORRECCIÓN PARA GUARDAR EN BASE DE DATOS ---
        # Java con Spring Data REST es muy estricto con las relaciones.
        # En lugar de mandar un objeto, mandamos la URL (URI List)
        
        datos = {
            "nombre": form.nombre.data,
            "activo": form.activo.data,
            "fechaInicio": str(form.fecha_inicio.data),
            # ESTO ES LO VITAL: La relación debe ser una URL String
            "empresa": { 
                "idEmpresa": form.id_empresa.data,  # Nombre según tu Base de Datos
                
            }
        }
        
        print(f"ENVIANDO A JAVA: {datos}") # Para que veas en terminal
        
        try:
            r = requests.post(f"{JAVA_API_URL}/proyectos", json=datos)
            
            if r.status_code in [200, 201]:
                flash("¡Proyecto guardado y vinculado!")
                return redirect(url_for('ver_proyectos'))
            else:
                print(f"ERROR JAVA: {r.text}")
                flash(f"Error Java: {r.status_code}")
                
        except Exception as e:
            flash(f"Error de conexión: {e}")

    return render_template('form_generico.html', form=form, titulo="Nuevo Proyecto")

@app.route('/proyectos/editar/<id>', methods=['GET', 'POST'])
def editar_proyecto(id):
    # URLs de la API Java
    url_proyecto = f'http://localhost:8080/api/proyectos/{id}'
    url_empresas = 'http://localhost:8080/api/empresas' # Para llenar el combo

    if request.method == 'POST':
        # --- GUARDAR (PUT) ---
        nombre = request.form['nombre']
        fecha = request.form['fecha'] # Viene como texto 'YYYY-MM-DD'
        id_empresa = request.form['empresa_id']
        activo = 'activo' in request.form
        
        # Armamos el JSON tal cual lo espera tu Entidad Java
        datos_actualizar = {
            "idProyecto": id, 
            "nombre": nombre,
            "fechaInicio": fecha,
            "activo": activo,
            # IMPORTANTE: Enviamos la empresa como objeto anidado con su ID
            "empresa": {
                "id": id_empresa 
            }
        }
        
        headers = {'Content-Type': 'application/json'}
        respuesta = requests.put(url_proyecto, json=datos_actualizar, headers=headers)
        
        if respuesta.status_code == 200 or respuesta.status_code == 204:
            return redirect('/proyectos')
        else:
            return f"Error al actualizar proyecto: {respuesta.text}"

    # --- MOSTRAR FORMULARIO (GET) ---
    
    # 1. Pedimos el proyecto actual
    resp_proy = requests.get(url_proyecto)
    # 2. Pedimos la lista de empresas para el menú desplegable
    resp_emp = requests.get(url_empresas)
    
    if resp_proy.status_code == 200 and resp_emp.status_code == 200:
        proyecto = resp_proy.json()
        empresas = resp_emp.json()
        
        # Enviamos ambas cosas al HTML
        return render_template('editar_proyecto.html', proyecto=proyecto, lista_empresas=empresas)
    else:
        return f"Error cargando datos. Proyecto: {resp_proy.status_code}, Empresas: {resp_emp.status_code}"

@app.route('/proyectos/eliminar/<id>')
def eliminar_proyecto(id):
    # 1. URL de la API
    url_api = f'http://localhost:8080/api/proyectos/{id}'
    
    # 2. Mandamos la orden de borrar
    respuesta = requests.delete(url_api)
    
    # 3. Verificamos y redirigimos
    if respuesta.status_code == 200 or respuesta.status_code == 204:
        return redirect('/proyectos')
    else:
        return f"Error al eliminar proyecto. Java respondió: {respuesta.status_code} - {respuesta.text}"

# RUTA REQUISITOS
@app.route('/requisitos')
def ver_requisitos():
    try:
        print("--- CONSULTANDO REQUISITOS ---")
        resp = requests.get(f"{JAVA_API_URL}/requisitos")
        data = resp.json()
        
        # DEBUG: Imprimir qué claves nos manda Java
        if "_embedded" in data:
            print("CLAVES ENCONTRADAS:", data["_embedded"].keys())
        else:
            print("JAVA NO DEVOLVIÓ DATOS EMBEBIDOS (Lista vacía o error)")
            
        lista = []
        
        # Spring Data REST a veces usa "requisitoes" o "requisitos"
        # Este código busca cualquiera de los dos
        if "_embedded" in data:
            # Intentamos sacar la lista con el nombre correcto
            items = data["_embedded"].get("requisitos") or data["_embedded"].get("requisitoes") or []
            
            for item in items:
                item["id"] = obtener_id_de_url(item["_links"]["self"]["href"])
                
                if "proyecto" in item["_links"]:
                    item["proyecto_id"] = obtener_id_de_url(item["_links"]["proyecto"]["href"])
                else:
                    item["proyecto_id"] = "N/A"
                lista.append(item)
                
        return render_template('lista_requisitos.html', lista=lista)
    except Exception as e:
        print(f"Error Requisitos: {e}")
        return render_template('lista_requisitos.html', lista=[])

@app.route('/requisitos/nuevo', methods=['GET', 'POST'])
def crear_requisito():
    form = FormularioRequisito()
    
    # Esto valida los datos (que el ID sea número, que no esté vacío, etc.)
    if form.validate_on_submit():
        datos = {
            "nombre": form.nombre.data,
            "descripcion": form.descripcion.data,
            "prioridad": form.prioridad.data,
            "estado": form.estado.data,
            "proyecto": { "idProyecto": form.id_proyecto.data }
        }
        try:
            r = requests.post(f"{JAVA_API_URL}/requisitos", json=datos)
            if r.status_code in [200, 201]:
                flash("Requisito guardado")
                return redirect(url_for('ver_requisitos'))
            else:
                print(f"ERROR JAVA: {r.text}")
                flash(f"Error Java: {r.status_code}")
        except Exception as e:
            flash(f"Error conexión: {e}")
    
    # --- AGREGAR ESTO PARA VER POR QUÉ FALLA EL GUARDADO ---
    else:
        if request.method == 'POST':
            print("!!! ERROR DE VALIDACIÓN EN PYTHON !!!")
            print(form.errors) # <--- ESTO TE DIRÁ QUÉ CAMPO ESTÁ MAL
    # -------------------------------------------------------

    return render_template('form_generico.html', form=form, titulo="Nuevo Requisito")

@app.route('/requisitos/editar/<id>', methods=['GET', 'POST'])
def editar_requisito(id):
    # URLs de la API
    url_req = f'http://localhost:8080/api/requisitos/{id}'
    url_proyectos = 'http://localhost:8080/api/proyectos' # Para el combo

    if request.method == 'POST':
        # --- GUARDAR (PUT) ---
        datos_actualizar = {
            "idRequisito": id,
            "codigo": request.form['codigo'],
            "nombre": request.form['nombre'],
            "descripcion": request.form['descripcion'],
            "capturadoPor": request.form['capturadoPor'],
            "prioridad": request.form['prioridad'], # Debe coincidir con el Enum en Java (ej. ALTA)
            "estado": request.form['estado'],       # Debe coincidir con el Enum (ej. activo)
            # Relación con Proyecto
            "proyecto": {
                "idProyecto": request.form['proyecto_id']
            }
        }
        
        headers = {'Content-Type': 'application/json'}
        respuesta = requests.put(url_req, json=datos_actualizar, headers=headers)
        
        if respuesta.status_code == 200 or respuesta.status_code == 204:
            return redirect('/requisitos')
        else:
            return f"Error al actualizar requisito: {respuesta.text}"

    # --- MOSTRAR FORMULARIO (GET) ---
    resp_req = requests.get(url_req)
    resp_proy = requests.get(url_proyectos)
    
    if resp_req.status_code == 200 and resp_proy.status_code == 200:
        return render_template('editar_requisito.html', 
                               requisito=resp_req.json(), 
                               lista_proyectos=resp_proy.json())
    else:
        return f"Error cargando datos. API Java respondió: {resp_req.status_code}"

@app.route('/requisitos/eliminar/<id>')
def eliminar_requisito(id):
    # 1. URL de la API
    url_api = f'http://localhost:8080/api/requisitos/{id}'
    
    # 2. Enviamos la orden DELETE a Java
    respuesta = requests.delete(url_api)
    
    # 3. Validamos
    if respuesta.status_code == 200 or respuesta.status_code == 204:
        return redirect('/requisitos')
    else:
        return f"Error al eliminar requisito. Java respondió: {respuesta.status_code} - {respuesta.text}"


# RUTA DIAGRAMAS
@app.route('/diagramas')
def ver_diagramas():
    try:
        resp = requests.get(f"{JAVA_API_URL}/diagramas")
        data = resp.json()
        lista = []
        
        if "_embedded" in data:
            for item in data["_embedded"]["diagramas"]:
                item["id"] = obtener_id_de_url(item["_links"]["self"]["href"])
                if "proyecto" in item["_links"]:
                    item["proyecto_id"] = obtener_id_de_url(item["_links"]["proyecto"]["href"])
                else:
                    item["proyecto_id"] = "N/A"
                lista.append(item)
                
        return render_template('lista_diagramas.html', lista=lista)
    except:
        return render_template('lista_diagramas.html', lista=[])

@app.route('/diagramas/nuevo', methods=['GET', 'POST'])
def crear_diagrama():
    form = FormularioDiagrama()
    
    if form.validate_on_submit():
        # Construimos el JSON
        datos = {
            "nombre": form.nombre.data,
            # Enviamos el tipo tal cual viene del formulario (minúsculas)
            "tipo_diagrama": form.tipo_diagrama.data, 
            "tipoDiagrama": form.tipo_diagrama.data, # Enviamos camelCase por si Java lo pide así
            "estado": form.estado.data,
            
            # LA RELACIÓN CON PROYECTO (CRÍTICO)
            "proyecto": { 
                "idProyecto": form.id_proyecto.data 
            }
        }
        
        try:
            r = requests.post(f"{JAVA_API_URL}/diagramas", json=datos)
            
            if r.status_code in [200, 201]:
                flash("Diagrama creado exitosamente")
                return redirect(url_for('ver_diagramas'))
            else:
                # Si falla Java, imprimimos por qué
                print(f"ERROR JAVA: {r.text}")
                flash(f"Error Java: {r.status_code}")
                
        except Exception as e:
            flash(f"Error de conexión: {e}")
            
    else:
        # SI FALLA LA VALIDACIÓN DE PYTHON (Formulario inválido)
        if request.method == 'POST':
            print("!!! ERROR VALIDACIÓN DIAGRAMA !!!")
            print(form.errors) # Mira la terminal negra si no hace nada

    return render_template('form_generico.html', form=form, titulo="Nuevo Diagrama")

@app.route('/diagramas/editar/<id>', methods=['GET', 'POST'])
def editar_diagrama(id):
    # URLs para conectar con Java
    url_diag = f'http://localhost:8080/api/diagramas/{id}'
    url_proy = 'http://localhost:8080/api/proyectos' # Para llenar el combo de proyectos

    if request.method == 'POST':
        # --- OPCIÓN 1: GUARDAR CAMBIOS (PUT) ---
        
        # 1. Armamos el JSON tal como lo espera tu Java
        datos_actualizar = {
            "idDiagrama": id, 
            "nombre": request.form['nombre'],
            "creadoPor": request.form['creadoPor'],
            "tipoDiagrama": request.form['tipo'], # Debe coincidir con el Enum en Java (ej. 'clases')
            "estado": request.form['estado'],     # Debe coincidir con el Enum en Java (ej. 'borrador')
            "archivoRuta": request.form['archivoRuta'],
            "proyecto": {
                "idProyecto": request.form['proyecto_id']
            }
        }
        
        # 2. Enviamos los datos a Java
        headers = {'Content-Type': 'application/json'}
        respuesta = requests.put(url_diag, json=datos_actualizar, headers=headers)
        
        # 3. Verificamos si funcionó
        if respuesta.status_code == 200 or respuesta.status_code == 204:
            return redirect('/diagramas') # Éxito: volvemos a la lista
        else:
            return f"Error al actualizar diagrama: {respuesta.text}"

    # --- OPCIÓN 2: MOSTRAR FORMULARIO (GET) ---
    
    # Pedimos los datos del diagrama y la lista de proyectos
    resp_diag = requests.get(url_diag)
    resp_proy = requests.get(url_proy)
    
    if resp_diag.status_code == 200 and resp_proy.status_code == 200:
        # Enviamos todo al HTML
        return render_template('editar_diagrama.html', 
                               diagrama=resp_diag.json(), 
                               lista_proyectos=resp_proy.json())
    else:
        return f"Error cargando datos. Java status - Diagrama: {resp_diag.status_code}, Proyectos: {resp_proy.status_code}"
    
@app.route('/diagramas/eliminar/<id>')
def eliminar_diagrama(id):
    # 1. URL de la API
    url_api = f'http://localhost:8080/api/diagramas/{id}'
    
    # 2. Enviamos la orden DELETE a Java
    respuesta = requests.delete(url_api)
    
    # 3. Validamos
    if respuesta.status_code == 200 or respuesta.status_code == 204:
        return redirect('/diagramas')
    else:
        return f"Error al eliminar diagrama. Java respondió: {respuesta.status_code} - {respuesta.text}"


if __name__ == '__main__':
    app.run(debug=True, port=5000)