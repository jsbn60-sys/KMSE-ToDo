<template id="user-tasks">
    <app-frame>
        <div class="row d-flex justify-content-center container">
            <div class="col-md-8">
                <div class="card-hover-shadow-2x mb-3 card">
                    <div class="card-header-tab card-header">
                        <div class="card-header-title font-size-lg text-capitalize font-weight-normal"><i class="fa fa-tasks"></i>&nbsp;Your task list, {{username}}!</div>
                    </div>
                    <div class="scroll-area-sm">
                        <perfect-scrollbar class="ps-show-limits">
                            <div style="position: static;" class="ps ps--active-y">
                                <div class="ps-content">
                                    <ul class=" list-group list-group-flush">
                                        <li v-for="task in tasks" class="list-group-item">
                                            <div class="todo-indicator bg-warning"></div>
                                            <div class="widget-content p-0">
                                                <div class="widget-content-wrapper">
                                                    <div class="widget-content-left mr-2">
                                                        <div class="custom-checkbox custom-control"> <input class="custom-control-input" id={{task.id}} type="checkbox"><label class="custom-control-label" for={{task.id}}>&nbsp;</label> </div>
                                                    </div>
                                                    <div class="widget-content-left">
                                                        <div class="widget-heading">{{task.title}} <div class="badge badge-danger ml-2">{{task.priority}}</div>
                                                        </div>
                                                        <div class="widget-subheading"><i>Deadline: {{task.planed}}</i></div>
                                                    </div>
                                                </div>
                                            </div>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </perfect-scrollbar>
                    </div>
                    <div class="d-block text-right card-footer"><a href="login" class="mr-2 btn btn-link btn-sm">Logout</a><a href="new-task" class="btn btn-primary">Add Task</a></div>
                </div>
            </div>
        </div>


    </app-frame>
</template>
<script>
    Vue.component("user-tasks", {
        template: "#user-tasks",
        data: () => ({
            username: localStorage.getItem("username"),
            tasks: [],
        }),
        created() {
            const token = localStorage.getItem("token");
            fetch("/api/me/tasks?token=" + token, {
                method: "GET",
            })
                .then( async res => {
                    if(res.status === 200) {
                        let resdata = await res.json();
                        console.log(resdata);
                        this.tasks = resdata;
                        console.log(this.tasks)
                    }
                })
                .catch(() => alert("Error while fetching tasks"));
        }
    });
</script>
