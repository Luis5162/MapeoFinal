from flask_wtf import FlaskForm
from wtforms import StringField, SubmitField, TextAreaField, BooleanField, DateField, IntegerField, SelectField
from wtforms.validators import DataRequired 

class FormularioEmpresa(FlaskForm):
    # El nombre ya lo tenías
    nombre = StringField('Nombre', validators=[DataRequired()])

    # --- AGREGA ESTOS 3 ---
    direccion = StringField('Dirección') 
    correo = StringField('Correo Electrónico') # Opcional: validators=[Email()] si quieres validar @
    telefono = StringField('Teléfono')
    # ----------------------

    # Estos ya los tenías
    activo = BooleanField('Activo', default=True)
    enviar = SubmitField('Guardar')

class FormularioProyecto(FlaskForm):
    nombre = StringField('Nombre del Proyecto', validators=[DataRequired()])
    # Fecha de inicio (Se verá como un calendario en el navegador)
    fecha_inicio = DateField('Fecha de Inicio', format='%Y-%m-%d', validators=[DataRequired()])
    activo = BooleanField('¿Está Activo?')
    
    # IMPORTANTE: Aquí necesitamos el ID de la empresa dueña
    # (Lo ideal sería una lista desplegable, pero empecemos simple con un número)
    id_empresa = IntegerField('ID de la Empresa Dueña', validators=[DataRequired()])
    
    enviar = SubmitField('Guardar Proyecto')
class FormularioRequisito(FlaskForm):
    # Coincide con tabla 'requisito'
    nombre = StringField('Nombre del Requisito', validators=[DataRequired()])
    descripcion = TextAreaField('Descripción')
    
    # Opciones basadas en tus datos (alta, media, baja)
    prioridad = SelectField('Prioridad', choices=[
        ('alta', 'Alta'), 
        ('media', 'Media'), 
        ('baja', 'Baja')
    ])
    
    # Opciones basadas en tus datos (activo, pendiente)
    estado = SelectField('Estado', choices=[
        ('activo', 'Activo'), 
        ('pendiente', 'Pendiente')
    ])
    
    id_proyecto = IntegerField('ID del Proyecto (Dueño)', validators=[DataRequired()])
    enviar = SubmitField('Guardar Requisito')

class FormularioDiagrama(FlaskForm):
    nombre = StringField('Nombre del Diagrama', validators=[DataRequired()])
    
    
    tipo_diagrama = SelectField('Tipo de Diagrama', choices=[
        ('clases', 'Clases'), 
        ('casos_de_uso', 'Casos de Uso'),  
        ('secuencia', 'Secuencia'),
        ('actividad', 'Actividad'),
        ('estados', 'Estados'),
        ('componentes', 'Componentes'),
        ('despliegue', 'Despliegue'),
        ('paquetes', 'Paquetes'),
        ('arquitectura', 'Arquitectura')
    ])
    
    
    estado = SelectField('Estado', choices=[
        ('borrador', 'Borrador'), 
        ('aprobado', 'Aprobado'),
        ('obsoleto', 'Obsoleto')
    ])
    
    id_proyecto = IntegerField('ID del Proyecto', validators=[DataRequired()])
    enviar = SubmitField('Guardar Diagrama')